var rights = JSON.parse("{" +
	"\"net-atos-entng-actualites-controllers-ActualitesController|updateThread\": \"net-atos-entng-actualites-controllers-ThreadController|updateThread\", " + 
	"\"net-atos-entng-actualites-controllers-ActualitesController|deleteThread\": \"net-atos-entng-actualites-controllers-ThreadController|deleteThread\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|shareThread\": \"net-atos-entng-actualites-controllers-ThreadController|shareThread\", " + 
	"\"net-atos-entng-actualites-controllers-ActualitesController|shareThreadRemove\": \"net-atos-entng-actualites-controllers-ThreadController|shareThreadRemove\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|shareThreadSubmit\": \"net-atos-entng-actualites-controllers-ThreadController|shareThreadSubmit\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|delete\": \"net-atos-entng-actualites-controllers-InfoController|delete\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|listThreadInfos\": \"net-atos-entng-actualites-controllers-InfoController|listInfosByThreadId\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|getThread\": \"net-atos-entng-actualites-controllers-ThreadController|getThread\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|updatePending\": \"net-atos-entng-actualites-controllers-InfoController|updatePending\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|unpublish\": \"net-atos-entng-actualites-controllers-InfoController|unpublish\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|publish\": \"net-atos-entng-actualites-controllers-InfoController|publish\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|updateDraft\": \"net-atos-entng-actualites-controllers-InfoController|updateDraft\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|submit\": \"net-atos-entng-actualites-controllers-InfoController|submit\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|trash\": \"net-atos-entng-actualites-controllers-InfoController|trash\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|getInfo\": \"net-atos-entng-actualites-controllers-InfoController|getInfo\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|createDraft\": \"net-atos-entng-actualites-controllers-InfoController|createDraft\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|restore\": \"net-atos-entng-actualites-controllers-InfoController|restore\", " +
	"\"net-atos-entng-actualites-controllers-ActualitesController|unsubmit\": \"net-atos-entng-actualites-controllers-InfoController|unsubmit\"" +
"}");

var info_old_rights = ["net-atos-entng-actualites-controllers-ActualitesController|comment",
                       "net-atos-entng-actualites-controllers-ActualitesController|deleteComment",
                       "net-atos-entng-actualites-controllers-ActualitesController|getInfo"];
var info_new_rights = ["net-atos-entng-actualites-controllers-CommentController|comment",
                       "net-atos-entng-actualites-controllers-CommentControllerr|deleteComment",
                       "net-atos-entng-actualites-controllers-InfoController|getInfo"];
var contrib_rights = ["net-atos-entng-actualites-controllers-InfoController|listInfosByThreadId",
                      "net-atos-entng-actualites-controllers-ThreadController|getThread",
                      "net-atos-entng-actualites-controllers-InfoController|createPending", 
                      "net-atos-entng-actualites-controllers-InfoController|shareInfo",
                      "net-atos-entng-actualites-controllers-InfoController|shareInfoRemove",
                      "net-atos-entng-actualites-controllers-InfoController|shareInfoSubmit" ];
var publish_rights = ["net-atos-entng-actualites-controllers-InfoController|createPublished",
					 "net-atos-entng-actualites-controllers-InfoController|updatePublished"];

print("BEGIN;");
db.actualites.threads.find().forEach(function(thread){
	if(thread.title && thread.owner.userId){
		// Insert user if not exist
		var user_query = "SELECT actualites.insert_user('" +
			thread.owner.userId + "', '" + thread.owner.displayName.replace(/'/g, "''") + "');";
		print(user_query);
		// thread entry
		var thread_query = "INSERT INTO actualites.thread (owner, created, modified, title";
		var thread_values = " VALUES ('"
			+ thread.owner.userId + "', '"
			+ thread.created.toISOString() + "', '"
			+ thread.modified.toISOString() + "', '"
			+ thread.title.replace(/'/g, "''") + "'";
		if(thread.icon){
			thread_query += ", icon";
			thread_values += ", '" + thread.icon + "'";
		}
		thread_query += ", mode)";
		thread_values += (thread.mode !== undefined) ? ", " + thread.mode + ");" : ", 0);";
		thread_query = thread_query + thread_values;
		print(thread_query);
		if(thread.shared && thread.shared.length > 0){
			// thread_share entry
			var thread_share_query = "INSERT INTO actualites.thread_shares VALUES ";
			var areThreadRights = false;
			thread.shared.forEach(function(share){
				var keys = Object.keys(share);
				var memeberId = (share.userId) ? share.userId : share.groupId;
				for(var i=1; i<keys.length; i++){
					if(share[keys[i]] === true && info_old_rights.indexOf(keys[i]) === -1){
						// getThread & listInfosByThreadId rights will be added with the contrib rights
						if(keys[i] !== "net-atos-entng-actualites-controllers-ActualitesController|getThread" &&
								keys[i] !== "net-atos-entng-actualites-controllers-ActualitesController|listThreadInfos"){
							areThreadRights = true;
							thread_share_query += "('" + memeberId + "', currval('actualites.thread_id_seq'), '" + rights[keys[i]] + "'), ";
						}
						// add new publish rights
						if(keys[i] === "net-atos-entng-actualites-controllers-ActualitesController|publish"){
							publish_rights.forEach(function(right){
								areThreadRights = true;
								thread_share_query += "('" + memeberId + "', currval('actualites.thread_id_seq'), '" + right + "'), ";
							});
						}
						// add new contrib rights
						if(keys[i] === "net-atos-entng-actualites-controllers-ActualitesController|createDraft"){
							contrib_rights.forEach(function(right){
								areThreadRights = true;
								thread_share_query += "('" + memeberId + "', currval('actualites.thread_id_seq'), '" + right + "'), ";
							});
						}
					}
				}
				// Insert user / group if not exist
				if(share.userId){
					var user_query = "SELECT actualites.insert_user('" +
						share.userId + "', null);";
					print(user_query);
				} else {
					var group_query = "SELECT actualites.insert_group('" +
						share.groupId + "', null);";
					print(group_query);
				}
			});
			if(areThreadRights){
				print(thread_share_query.replace(/, $/, ";"));
			}
		}
		if(thread.infos){
			thread.infos.forEach(function(info){
				if(info.title && info.status !== undefined && info.owner.userId){
					// Insert user if not exist
					var user_query = "SELECT actualites.insert_user('" +
						info.owner.userId + "', '" + info.owner.displayName.replace(/'/g, "''") + "');";
					print(user_query);
					var info_query = "INSERT INTO actualites.info (owner, created, modified, title";
					var info_values = " VALUES ('"
						+ info.owner.userId
						+ "', '" + info.created.toISOString()
						+ "', '" + info.modified.toISOString()
						+ "', '" + info.title.replace(/'/g, "''");
					if(info.content){
						info_query += ", content";
						info_values += "', '" + info.content.replace(/'/g, "''");
					}
					info_query += ", status";
					info_values += "', " + info.status;
					if(info.publicationDate){
						info_query += ", publication_date";
						if(typeof info.publicationDate === 'string'){
							info_values += ", '" + info.publicationDate + "'";
						}
						else{
							info_values += ", '" + info.publicationDate.toISOString() + "'";
						}
					}
					if(info.expirationDate){
						info_query += ", expiration_date";
						if(typeof info.expirationDate === 'string'){
							info_values += ", '" + info.expirationDate + "'";
						}
						else{
							info_values += ", '" + info.expirationDate.toISOString() + "'";
						}
					}
					info_query += ", is_headline, thread_id)";
					info_values += (info.isHeadline !== undefined) ? ", " + info.isHeadline : ", false";
					info_values += ", currval('actualites.thread_id_seq'));";
					info_query = info_query + info_values;
					print(info_query);
					if(thread.shared && thread.shared.length > 0){
						// info_share entry
						var info_share_query = "INSERT INTO actualites.info_shares VALUES ";
						thread.shared.forEach(function(share){
							var keys = Object.keys(share);
							var memeberId = (share.userId) ? share.userId : share.groupId;
							var can_read = false;
							for(var i=1; !can_read && i<keys.length; i++){
								// can read info ?
								if(share[keys[i]] === true && info_old_rights.indexOf(keys[i]) !== -1){
									can_read = true;
									info_new_rights.forEach(function(right){
										info_share_query += "('" + memeberId + "', currval('actualites.info_id_seq'), '" + right + "'), ";
									});
								}
							}
							// Insert user / group if not exist
							if(share.userId){
								var user_query = "SELECT actualites.insert_user('" +
									share.userId + "', null);";
								print(user_query);
							} else {
								var group_query = "SELECT actualites.insert_group('" +
									share.groupId + "', null);";
								print(group_query);
							}
						});
						print(info_share_query.replace(/, $/, ";"));
					}
					if(info.comments){
						info.comments.forEach(function(comment){
							if(comment.comment && comment.author){
                // Insert user if not exist
                var user_query = "SELECT actualites.insert_user('" +
                comment.author.replace(/'/g, "''") + "', '" + comment.authorName.replace(/'/g, "''") + "');";
                print(user_query);
								var comment_query = "INSERT INTO actualites.comment (owner, created, modified, comment, info_id) VALUES ('"
									+ comment.author.replace(/'/g, "''") + "', '"
									+ comment.posted.toISOString() + "', '"
									+ comment.posted.toISOString() + "', '"
									+ comment.comment.replace(/'/g, "''") + "', "
									+ "currval('actualites.info_id_seq'));";
							}
							print(comment_query);
						});
					}
				}
			});
		}
	}
});
print("COMMIT;");