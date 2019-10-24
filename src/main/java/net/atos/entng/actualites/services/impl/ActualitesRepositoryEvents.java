/*
 * Copyright © Région Nord Pas de Calais-Picardie,  Département 91, Région Aquitaine-Limousin-Poitou-Charentes, 2016.
 *
 * This file is part of OPEN ENT NG. OPEN ENT NG is a versatile ENT Project based on the JVM and ENT Core Project.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation (version 3 of the License).
 *
 * For the sake of explanation, any module that communicate over native
 * Web protocols, such as HTTP, with OPEN ENT NG is outside the scope of this
 * license and could be license under its own terms. This is merely considered
 * normal use of OPEN ENT NG, and does not fall under the heading of "covered work".
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package net.atos.entng.actualites.services.impl;

import static net.atos.entng.actualites.Actualites.MANAGE_RIGHT_ACTION;

import io.vertx.core.Vertx;
import org.entcore.common.service.impl.SqlRepositoryEvents;
import org.entcore.common.sql.Sql;
import org.entcore.common.sql.SqlResult;
import org.entcore.common.sql.SqlStatementsBuilder;
import org.entcore.common.user.RepositoryEvents;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import fr.wseduc.webutils.Either;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActualitesRepositoryEvents extends SqlRepositoryEvents {

	private static final Logger log = LoggerFactory.getLogger(ActualitesRepositoryEvents.class);
	private final boolean shareOldGroupsToUsers;

	public ActualitesRepositoryEvents(boolean shareOldGroupsToUsers, Vertx vertx) {
		super(vertx);
		this.shareOldGroupsToUsers = shareOldGroupsToUsers;
	}

	@Override
	public void exportResources(String exportId, String userId, JsonArray groups, String exportPath, String locale, String host, final Handler<Boolean> handler) {

			final HashMap<String,JsonArray> queries = new HashMap<String, JsonArray>();

			final String commentTable = "actualites.comment",
					groupTable = "actualites.groups",
					infoTable = "actualites.info",
					infoRevisionTable = "actualites.info_revision",
					infoSharesTable = "actualites.info_shares",
					membersTable = "actualites.members",
					threadTable = "actualites.thread",
					threadSharesTable = "actualites.thread_shares",
					usersTable = "actualites.users";

			JsonArray params = new JsonArray().add(userId).add(userId).addAll(groups);

			String queryInfo =
					"SELECT DISTINCT info.* " +
							"FROM " + infoTable + " " +
							"LEFT JOIN " + infoSharesTable + " infoS ON info.id = infoS.resource_id " +
							"LEFT JOIN " + membersTable + " mem ON infoS.member_id = mem.id " +
							"WHERE info.owner = ? " +
							"OR mem.user_id = ? " +
							((groups != null && !groups.isEmpty()) ? "OR mem.group_id IN " + Sql.listPrepared(groups.getList()) : "");
			queries.put(infoTable,new SqlStatementsBuilder().prepared(queryInfo,params).build());

			String queryInfoRevision =
					"SELECT DISTINCT infoR.* " +
							"FROM " + infoRevisionTable + " infoR " +
							"WHERE infoR.info_id IN (" + queryInfo.replace("*","id") + ")";
			queries.put(infoRevisionTable,new SqlStatementsBuilder().prepared(queryInfoRevision,params).build());


			String queryThread =
					"SELECT DISTINCT th.* " +
							"FROM " + threadTable + " th " +
							"LEFT JOIN "+ threadSharesTable + " thS ON th.id = thS.resource_id " +
							"LEFT JOIN " + membersTable + " mem ON thS.member_id = mem.id " +
							"WHERE th.owner = ? " +
							"OR mem.user_id = ? " +
							((groups != null && !groups.isEmpty()) ? "OR mem.group_id IN " + Sql.listPrepared(groups.getList()) : "") + " " +
							"OR th.id IN (" + queryInfo.replace("*","thread_id") + ")";
			queries.put(threadTable,new SqlStatementsBuilder().prepared(queryThread,new JsonArray().addAll(params).addAll(params)).build());


			AtomicBoolean exported = new AtomicBoolean(false);

			createExportDirectory(exportPath, locale, new Handler<String>() {
				@Override
				public void handle(String path) {
					if (path != null) {
						exportTables(queries, new JsonArray(), path, exported, handler);
					}
					else {
						handler.handle(exported.get());
					}
				}
			});
	}

	@Override
	public void deleteGroups(JsonArray groups) {
		if(groups != null && groups.size() > 0) {
			final JsonArray gIds = new fr.wseduc.webutils.collections.JsonArray();
			for (Object o : groups) {
				if (!(o instanceof JsonObject)) continue;
				final JsonObject j = (JsonObject) o;
				gIds.add(j.getString("group"));
			}
            if (gIds.size() > 0) {
                // Delete the groups. Cascade delete : delete from members, thread_shares and info_shares too
                Sql.getInstance().prepared("DELETE FROM actualites.groups WHERE id IN " + Sql.listPrepared(gIds.getList())
                        , gIds, SqlResult.validRowsResultHandler(new Handler<Either<String, JsonObject>>() {
                    @Override
                    public void handle(Either<String, JsonObject> event) {
                        if (event.isRight()) {
                            log.info("[ActualitesRepositoryEvents][deleteGroups]The groups and their shares are deleted");
                        } else {
                            log.error("[ActualitesRepositoryEvents][deleteGroups] Error deleting the groups and their shares. Message : " + event.left().getValue());
                        }
                    }
                }));
            }
		} else {
			log.warn("[ActualitesRepositoryEvents][deleteGroups] groups is null or empty");
		}
	}

	@Override
	public void deleteUsers(JsonArray users) {
        //FIXME: anonymization is not relevant
		if (users != null && users.size() > 0) {
			final JsonArray uIds = new fr.wseduc.webutils.collections.JsonArray();
			for (Object u : users) {
				if (!(u instanceof JsonObject)) continue;
				final JsonObject j = (JsonObject) u;
				uIds.add(j.getString("id"));
			}
			SqlStatementsBuilder statementsBuilder = new SqlStatementsBuilder();
			// Remove all thread shares from thread_shares table
			statementsBuilder.prepared("DELETE FROM actualites.thread_shares WHERE member_id IN " + Sql.listPrepared(uIds.getList()), uIds);
			// Remove all news shares from info_shares table
			statementsBuilder.prepared("DELETE FROM actualites.info_shares WHERE member_id IN " + Sql.listPrepared(uIds.getList()), uIds);
			// Delete users (Set deleted = true in users table)
			statementsBuilder.prepared("UPDATE actualites.users SET deleted = true WHERE id IN " + Sql.listPrepared(uIds.getList()), uIds);
			// Delete all threads where the owner is deleted and no manager rights shared on these resources
			// Cascade delete : the news that belong to these threads will be deleted too
			// thus, no need to delete news that do not have a manager because the thread owner is still there
			statementsBuilder.prepared("DELETE FROM actualites.thread" +
									  " USING (" +
										  " SELECT DISTINCT t.id, count(ts.member_id) AS managers" +
										  " FROM actualites.thread AS t" +
										  " LEFT OUTER JOIN actualites.thread_shares AS ts ON t.id = ts.resource_id AND ts.action = ?" +
										  " LEFT OUTER JOIN actualites.users AS u ON t.owner = u.id" +
										  " WHERE u.deleted = true" +
										  " GROUP BY t.id" +
									 " ) a" +
									 " WHERE actualites.thread.id = a.id" +
									 " AND a.managers = 0"
								  	  , new fr.wseduc.webutils.collections.JsonArray().add(MANAGE_RIGHT_ACTION));
			Sql.getInstance().transaction(statementsBuilder.build(), SqlResult.validRowsResultHandler(new Handler<Either<String, JsonObject>>() {
				@Override
				public void handle(Either<String, JsonObject> event) {
					if (event.isRight()) {
						log.info("[ActualitesRepositoryEvents][cleanDataBase] The resources created by users are deleted");
                    } else {
						log.error("[ActualitesRepositoryEvents][cleanDataBase] Error deleting the resources created by users. Message : " + event.left().getValue());
					}
				}
			}));
		} else {
			log.warn("[ActualitesRepositoryEvents][deleteUsers] users is empty");
		}
	}
}
