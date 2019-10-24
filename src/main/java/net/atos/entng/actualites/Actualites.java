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

package net.atos.entng.actualites;

import net.atos.entng.actualites.controllers.CommentController;
import net.atos.entng.actualites.controllers.InfoController;
import net.atos.entng.actualites.controllers.ThreadController;
import net.atos.entng.actualites.controllers.DisplayController;
import net.atos.entng.actualites.services.impl.ActualitesRepositoryEvents;

import net.atos.entng.actualites.services.impl.ActualitesSearchingEvents;
import org.entcore.common.http.BaseServer;
import org.entcore.common.http.filter.ShareAndOwner;
import org.entcore.common.service.impl.SqlCrudService;
import org.entcore.common.service.impl.SqlSearchService;
import org.entcore.common.share.impl.SqlShareService;
import org.entcore.common.sql.SqlConf;
import org.entcore.common.sql.SqlConfs;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;

import java.util.ArrayList;
import java.util.List;

public class Actualites extends BaseServer {
	public final static String NEWS_SCHEMA = "actualites";

	public final static String THREAD_RESOURCE_ID = "threadid";
	public final static String THREAD_TABLE = "thread";
	public final static String THREAD_SHARE_TABLE = "thread_shares";

	public final static String INFO_RESOURCE_ID = "infoid";
	public final static String INFO_TABLE = "info";
	public final static String INFO_SHARE_TABLE = "info_shares";
	public final static String INFO_REVISION_TABLE = "info_revision";

	public final static String COMMENT_TABLE = "comment";

	public final static String MANAGE_RIGHT_ACTION = "net-atos-entng-actualites-controllers-ThreadController|updateThread";

	@Override
	public void start() throws Exception {
		super.start();
		final EventBus eb = getEventBus(vertx);

		// Subscribe to events published for transition
		setRepositoryEvents(new ActualitesRepositoryEvents(config.getBoolean("share-old-groups-to-users", false),vertx));

		if (config.getBoolean("searching-event", true)) {
			final List<String> searchFields = new ArrayList<String>();
			searchFields.add("text_searchable");
			setSearchingEvents(new ActualitesSearchingEvents(new SqlSearchService(getSchema(), INFO_TABLE, INFO_SHARE_TABLE, searchFields)));
		}

		addController(new DisplayController());

		// set default rights filter
		setDefaultResourceFilter(new ShareAndOwner());

		// thread table
		SqlConf confThread = SqlConfs.createConf(ThreadController.class.getName());
		confThread.setResourceIdLabel(THREAD_RESOURCE_ID);
		confThread.setTable(THREAD_TABLE);
		confThread.setShareTable(THREAD_SHARE_TABLE);
		confThread.setSchema(getSchema());

		// thread controller
		ThreadController threadController = new ThreadController();
		SqlCrudService threadSqlCrudService = new SqlCrudService(getSchema(), THREAD_TABLE, THREAD_SHARE_TABLE, new JsonArray().add("*"), new JsonArray().add("*"), true);
		threadController.setCrudService(threadSqlCrudService);
		threadController.setShareService(new SqlShareService(getSchema(),THREAD_SHARE_TABLE, eb, securedActions, null));
		addController(threadController);

		// info table
		SqlConf confInfo = SqlConfs.createConf(InfoController.class.getName());
		confInfo.setResourceIdLabel(INFO_RESOURCE_ID);
		confInfo.setTable(INFO_TABLE);
		confInfo.setShareTable(INFO_SHARE_TABLE);
		confInfo.setSchema(getSchema());

		// info controller
		InfoController infoController = new InfoController();
		SqlCrudService infoSqlCrudService = new SqlCrudService(getSchema(), INFO_TABLE, INFO_SHARE_TABLE, new JsonArray().add("*"), new JsonArray().add("*"), true);
		infoController.setCrudService(infoSqlCrudService);
		infoController.setShareService(new SqlShareService(getSchema(),INFO_SHARE_TABLE, eb, securedActions, null));
		addController(infoController);

		// comment table
		SqlConf confComment = SqlConfs.createConf(CommentController.class.getName());
		confComment.setTable(COMMENT_TABLE);
		confComment.setSchema(getSchema());

		// comment controller
		CommentController commentController = new CommentController();
		SqlCrudService commentSqlCrudService = new SqlCrudService(getSchema(), COMMENT_TABLE);
		commentController.setCrudService(commentSqlCrudService);
		addController(commentController);

	}

}
