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

package net.atos.entng.actualites.services;

import fr.wseduc.webutils.Either;
import org.entcore.common.user.UserInfos;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface InfoService {

	/**
	 * Create new info in database and create a new revision.
	 * @param data new info
	 * @param user user creating info
	 * @param eventStatus revision event name
	 * @param handler result handler
	 */
	public void create(JsonObject data, UserInfos user, String eventStatus, final Handler<Either<String, JsonObject>> handler);

	/**
	 * Update info in database and create a new revision.
	 * @param id info id
	 * @param data info values
	 * @param user user updating info
	 * @param eventStatus revision event name
	 * @param handler result handler
	 */
	public void update(String id, JsonObject data, UserInfos user, String eventStatus, Handler<Either<String, JsonObject>> handler);

	public void retrieve(String id, Handler<Either<String, JsonObject>> handler);
	
	public void retrieve(String id, UserInfos user, Handler<Either<String, JsonObject>> handler);

	public void list(UserInfos user, Handler<Either<String, JsonArray>> handler);

	public void listByThreadId(String id, UserInfos user, Handler<Either<String, JsonArray>> handler);

	public void listLastPublishedInfos(UserInfos user, int resultSize, Handler<Either<String, JsonArray>> handler);

	public void listForLinker(UserInfos user, Handler<Either<String, JsonArray>> handler);

	public void getSharedWithIds(String infoId, Handler<Either<String, JsonArray>> handler);

	/**
	 * Get revisions filtered on infoId.
	 * @param infoId info id.
	 * @param handler result handler.
	 */
	public void getRevisions(Long infoId, Handler<Either<String, JsonArray>> handler);

	public void getOwnerInfo(String infoId, Handler<Either<String, JsonObject>> handler);

}
