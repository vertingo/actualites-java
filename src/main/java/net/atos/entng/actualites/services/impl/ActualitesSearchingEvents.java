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

import fr.wseduc.webutils.Either;
import fr.wseduc.webutils.Either.Right;
import org.entcore.common.search.SearchingEvents;
import org.entcore.common.service.SearchService;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;

public class ActualitesSearchingEvents implements SearchingEvents {

	private static final Logger log = LoggerFactory.getLogger(ActualitesSearchingEvents.class);
	private SearchService searchService;

	public ActualitesSearchingEvents(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	public void searchResource(List<String> appFilters, final String userId, JsonArray groupIds, JsonArray searchWords, Integer page, Integer limit, final JsonArray columnsHeader,
							   final String locale, final Handler<Either<String, JsonArray>> handler) {
		if (appFilters.contains(ActualitesSearchingEvents.class.getSimpleName())) {
			final List<String> returnFields = new ArrayList<String>();
			returnFields.add("title");
			returnFields.add("content");
			returnFields.add("owner");
			returnFields.add("modified");
			returnFields.add("thread_id");
			returnFields.add("id");
			returnFields.add("status");

			searchService.search(userId, groupIds.getList(), returnFields, searchWords.getList(), page, limit, new Handler<Either<String, JsonArray>>() {
				@Override
				public void handle(Either<String, JsonArray> event) {
					if (event.isRight()) {
						final JsonArray res = formatSearchResult(event.right().getValue(), columnsHeader, userId);
						handler.handle(new Right<String, JsonArray>(res));
					} else {
						handler.handle(new Either.Left<String, JsonArray>(event.left().getValue()));
					}
					if (log.isDebugEnabled()) {
						log.debug("[ActualitesSearchingEvents][searchResource] The resources searched by user are finded");
					}
				}
			});
		} else {
			handler.handle(new Right<String, JsonArray>(new fr.wseduc.webutils.collections.JsonArray()));
		}
	}

	private JsonArray formatSearchResult(final JsonArray results, final JsonArray columnsHeader, final String userId) {
		final List<String> aHeader = columnsHeader.getList();
		final JsonArray traity = new fr.wseduc.webutils.collections.JsonArray();

		for (int i=0;i<results.size();i++) {
			final JsonObject j = results.getJsonObject(i);
			//Only published (status == 3) or owner
			if (j != null && (j.getInteger("status", 0).equals(3) || j.getString("owner", "").equals(userId))) {
				final JsonObject jr = new JsonObject();
				jr.put(aHeader.get(0), j.getString("title"));
				jr.put(aHeader.get(1), j.getString("content"));
				jr.put(aHeader.get(2), new JsonObject().put("$date",
						DatatypeConverter.parseDateTime(j.getString("modified")).getTime().getTime()));
				jr.put(aHeader.get(3), j.getString("username"));
				jr.put(aHeader.get(4), j.getString("owner"));
				jr.put(aHeader.get(5), "/actualites#/view/thread/"+
						j.getLong("thread_id",0l) + "/info/"+j.getLong("id",0l));
				traity.add(jr);
			}
		}
		return traity;
	}
}
