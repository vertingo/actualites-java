package net.atos.entng.actualites.test.integration

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import org.entcore.test.appregistry.Role

object ActualitesScenario {

	val scn =
    Role.createAndSetRole("Actualites")
    /*.exec(session => {
        session.setAttribute("actualitesInfoStateDraft", 1)
          .setAttribute("actualitesInfoStatePending", 2)
          .setAttribute("actualitesInfoStatePublished", 3)
          .setAttribute("actualitesInfoStateTrash", 0)})*/
    .exec(http("Login teacher")
      .post("""/auth/login""")
      .formParam("""email""", """${teacherLogin}""")
      .formParam("""password""", """blipblop""")
      .check(status.is(302)))
  // *****************************************************************
  // **                           Threads                           **
  // *****************************************************************/
  // Create Thread
  .exec(http("Thread Create")
    .post("/actualites/thread")
    .body(StringBody("""{"title" : "thread created", "mode" : 0}"""))
    .check(status.is(200),
        jsonPath("$.id").find.saveAs("threadId")
      ))
  // Get the thread created
  .exec(http("Thread Get")
    .get("/actualites/thread/${threadId}")
    .check(status.is(200),
        jsonPath("$._id").find.is("${threadId}"),
        jsonPath("$.title").find.is("thread created")
      ))
  // update the thread
  .exec(http("Thread Update")
    .put("/actualites/thread/${threadId}")
    .body(StringBody("""{"title" : "thread updated"}"""))
    .check(status.is(200)))
  // Check if the thread was updated
  .exec(http("Thread Get updated")
    .get("/actualites/thread/${threadId}")
    .check(status.is(200),
        jsonPath("$.title").find.is("thread updated")
      ))
  // Get all threads
  .exec(http("Thread List")
    .get("/actualites/threads")
    .check(status.is(200),
        jsonPath("$[0]._id").find.is("${threadId}")
      ))

  // *****************************************************************
  // **                            News                             **
  // *****************************************************************/
  // Tests done using a teacher account who is the thread owner

  // Create news
  .exec(http("Info Create")
    .post("/actualites/thread/${threadId}/info")
    .body(StringBody("""{"thread_id" : ${threadId}, "title" : "info created", "content": "info content created", "status": 42}""")) // status to check it is ignored
    .check(status.is(200),
        jsonPath("$.id").find.saveAs("infoId")
      ))
  .exec(http("Info Create")
    .post("/actualites/thread/${threadId}/info")
    .body(StringBody("""{"thread_id" : ${threadId}, "title" : "info created", "content": "info content created", "status": 42}""")) // status to check it is ignored
    .check(status.is(200),
        jsonPath("$.id").find.saveAs("otherInfoId")
      ))
  // Get the news created
  .exec(http("Info Get")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$._id").find.is("${infoId}"),
        jsonPath("$.title").find.is("info created"),
        jsonPath("$.content").find.is("info content created"),
        jsonPath("$.status").find.is("1")
      ))
  // Update the news
  .exec(http("Info Update")
    .put("/actualites/thread/${threadId}/info/${infoId}/draft")
    .body(StringBody("""{"title" : "info updated", "content": "info content updated", "status": 42}""")) // status to check it is ignored
    .check(status.is(200)))
  // Check if the news was updated
  .exec(http("Info Get updated")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.title").find.is("info updated"),
        jsonPath("$.content").find.is("info content updated"),
        jsonPath("$.status").find.is("1")
      ))
  // Get all news by thread id
  .exec(http("Thread Info List")
    .get("/actualites/thread/${threadId}/infos")
    .check(status.is(200)
      ))
  // Get all news
  .exec(http("Global Info List")
    .get("/actualites/infos")
    .check(status.is(200)
      ))
  // Submit the news
  .exec(http("Info Submit")
    .put("/actualites/thread/${threadId}/info/${infoId}/submit")
	.body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(200)))
  .exec(http("Info Submit")
    .put("/actualites/thread/${threadId}/info/${otherInfoId}/submit")
  .body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(200)))
  // Check if the news was submitted
  .exec(http("Info Get submitted")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.status").find.is("2")
      ))
  // Publish the news
  .exec(http("Info Publish")
    .put("/actualites/thread/${threadId}/info/${infoId}/publish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(200)))
  .exec(http("Info Publish")
    .put("/actualites/thread/${threadId}/info/${otherInfoId}/publish")
  .body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(200)))
  // Check if the news was published
  .exec(http("Info Get published")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.status").find.is("3")
      ))
  .exec(http("Info Get published")
    .get("/actualites/thread/${threadId}/info/${otherInfoId}")
    .check(status.is(200),
        jsonPath("$.status").find.is("3")
      ))
  // Unpublish the news
  .exec(http("Info Unpublish")
    .put("/actualites/thread/${threadId}/info/${infoId}/unpublish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(200)))
  // Check if the news was unpublished
  .exec(http("Info Get after unpublish")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.status").find.is("2")
      ))
  // Unsubmit the news
  .exec(http("Info Unsubmit")
    .put("/actualites/thread/${threadId}/info/${infoId}/unsubmit")
	.body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(200)))
  // Check if the news was unsubmitted
  .exec(http("Info Get after unsubmit")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.status").find.is("1")
      ))
  .exec(http("Logout 1 - teacher")
    .get("""/auth/logout""")
    .check(status.is(302)))

  // *****************************************************************
  // **                          Share rights                       **
  // *****************************************************************/

  val scnNoRights =
  exec(http("Login 2 - student")
    .post("""/auth/login""")
    .formParam("""email""", """${studentLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // Tests done using a student account who :
  // has no right on the thread
  // has no right on the news
   
  .exec(http("Thread Get (nr)")
    .get("/actualites/thread/${threadId}")
    .check(status.is(401)))
  .exec(http("Thread Update (nr)")
    .put("/actualites/thread/${threadId}")
    .body(StringBody("""{"name" : "thread not updated"}"""))
    .check(status.is(401)))
  .exec(http("Thread Info List (nr)")
    .get("/actualites/thread/${threadId}/infos")
    .check(status.is(401)))
  .exec(http("Info Create (nr)")
    .post("/actualites/thread/${threadId}/info")
    .body(StringBody("""{"thread_id" : ${threadId}, "title" : "info not created", "content": "info not created"}"""))
    .check(status.is(401)))
  .exec(http("Info Get (nr)")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(401)))
  .exec(http("Info Update (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/draft")
    .body(StringBody("""{"title" : "info not updated", "content": "info not updated"}"""))
    .check(status.is(401)))
  .exec(http("Delete Info (nr)")
    .delete("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(401)))
  .exec(http("Info Comment (nr)")
    .put("/actualites/info/${infoId}/comment")
    .body(StringBody("""{"info_id" : ${infoId}, "title" : "info not updated", "comment" : "student comment"}"""))
    .check(status.is(401)))
  .exec(http("Info Submit (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/submit")
	.body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(401)))
  .exec(http("Info Publish (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/publish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(401)))
  .exec(http("Info Unpublish (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unpublish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(401)))
  .exec(http("Info Unsubmit (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unsubmit")
	.body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(401)))
  .exec(http("Logout 2 - student")
    .get("""/auth/logout""")
    .check(status.is(302)))

  val scnRead =
  exec(http("Login 3 - teacher")
    .post("""/auth/login""")
    .formParam("""email""", """${teacherLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))
  .exec(http("Get share json")
    .get("/actualites/thread/${threadId}/info/share/json/${infoId}")
    .check(status.is(200)))

  // The teacher (news owner) shares read right with the student on the news
  .exec(http("Share Read permission with Student as a Person")
    .put("/actualites/thread/${threadId}/info/share/json/${infoId}")
    .bodyPart(StringBodyPart("userId", "${studentId}"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|getInfo"))
    .check(status.is(200)))
  // Publish the news, so that the student can read it
  .exec(http("Info Publish")
    .put("/actualites/thread/${threadId}/info/${infoId}/publish")
  .body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(200)))
  .exec(http("Logout 3 - teacher")
    .get("""/auth/logout""")
    .check(status.is(302)))
  .exec(http("Login 4 - student")
    .post("""/auth/login""")
    .formParam("""email""", """${studentLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // Tests done using a student account who :
  // has no right on the thread
  // has Read right on the news

  .exec(http("Thread Get (nr)")
    .get("/actualites/thread/${threadId}")
    .check(status.is(401)))
  .exec(http("Thread Update (nr)")
    .put("/actualites/thread/${threadId}")
    .body(StringBody("""{"name" : "thread not updated"}"""))
    .check(status.is(401)))
  .exec(http("Thread Info List (nr)")
    .get("/actualites/thread/${threadId}/infos")
    .check(status.is(401)))
  .exec(http("Info Create (nr)")
    .post("/actualites/thread/${threadId}/info")
    .body(StringBody("""{"thread_id" : ${threadId}, "title" : "info not created", "content": "info not created"}"""))
    .check(status.is(401)))
  .exec(http("Info List")
    .get("/actualites/infos")
    .check(status.is(200),
      jsonPath("$[?(@._id == ${infoId})].status").is("3")
      ))
  .exec(http("Info Get (nr)")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$._id").find.is("${infoId}"),
        jsonPath("$.title").find.is("info updated"),
        jsonPath("$.content").find.is("info content updated"),
        jsonPath("$.status").find.is("3")
      ))
  .exec(http("Info Update (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/draft")
    .body(StringBody("""{"title" : "info not updated", "content": "info not updated"}"""))
    .check(status.is(401)))
  .exec(http("Delete Info (nr)")
    .delete("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(401)))
  .exec(http("Info Comment (nr)")
    .put("/actualites/info/${infoId}/comment")
    .body(StringBody("""{"info_id" : ${infoId}, "title" : "info not updated", "comment" : "student comment"}"""))
    .check(status.is(401)))
  .exec(http("Info Submit (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/submit")
  .body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(401)))
  .exec(http("Info Publish (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/publish")
  .body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(401)))
  .exec(http("Info Unpublish (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unpublish")
  .body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(401)))
  .exec(http("Info Unsubmit (nr)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unsubmit")
  .body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(401)))
  .exec(http("Logout 4 - student")
    .get("""/auth/logout""")
    .check(status.is(302)))

  val scnComment =
  exec(http("Login 5 - teacher")
    .post("""/auth/login""")
    .formParam("""email""", """${teacherLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // The teacher (news owner) shares comment right with the student on the news
  .exec(http("Share Comment permission with Student as a Person")
    .put("/actualites/thread/${threadId}/info/share/json/${infoId}")
    .bodyPart(StringBodyPart("userId", "${studentId}"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-CommentController|comment"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-CommentController|deleteComment"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-CommentController|updateComment"))
    .check(status.is(200)))
  .exec(http("Logout 5 - teacher")
    .get("""/auth/logout""")
    .check(status.is(302)))
  .exec(http("Login 6 - student")
    .post("""/auth/login""")
    .formParam("""email""", """${studentLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // Tests done using a student account who :
  // has no right on the thread
  // has Read and comment rights on the news
   
  // Add comment
  .exec(http("Info add comment")
    .put("/actualites/info/${infoId}/comment")
    .body(StringBody("""{"info_id" : ${infoId}, "title" : "info updated", "comment" : "student comment"}"""))
    .check(status.is(200),
        jsonPath("$.id").find.saveAs("commentId")
      ))
  // Delete comment
  .exec(http("Info delete comment")
    .delete("/actualites/info/${infoId}/comment/${commentId}")
    .check(status.is(200)))
  .exec(http("Logout 6 - student")
    .get("""/auth/logout""")
    .check(status.is(302)))

  val scnContrib =
  exec(http("Login 7 - teacher")
    .post("""/auth/login""")
    .formParam("""email""", """${teacherLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // The teacher (thread owner) shares contrib right with the student on the thread
  .exec(http("Share Contrib permission with Student as a Person")
    .put("/actualites/thread/share/json/${threadId}")
    .bodyPart(StringBodyPart("userId", "${studentId}"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|createDraft"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|createPending"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|listInfosByThreadId"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|shareInfo"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|shareInfoRemove"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|shareInfoSubmit"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|submit"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|unsubmit"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|updateDraft"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-ThreadController|getThread"))
    .check(status.is(200)))
  .exec(http("Logout 7 - teacher")
    .get("""/auth/logout""")
    .check(status.is(302)))
  .exec(http("Login 8 - student")
    .post("""/auth/login""")
    .formParam("""email""", """${studentLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // Tests done using a student account who :
  // has Read and contrib rights on the thread
  // has Read and comment rights on the news
   
  // can
  .exec(http("Info Create (sc)")
    .post("/actualites/thread/${threadId}/info")
    .body(StringBody("""{"thread_id" : ${threadId}, "title" : "info created shared contrib", "content": "info content created shared contrib"}"""))
    .check(status.is(200),
        jsonPath("$.id").find.saveAs("studentContribInfoId")
      ))
  .exec(http("Info Get (sc)")
    .get("/actualites/thread/${threadId}/info/${studentContribInfoId}")
    .check(status.is(200),
        jsonPath("$._id").find.is("${studentContribInfoId}"),
        jsonPath("$.title").find.is("info created shared contrib"),
        jsonPath("$.content").find.is("info content created shared contrib"),
        jsonPath("$.status").find.is("1")
      ))
  .exec(http("Info Update draft (sc)")
    .put("/actualites/thread/${threadId}/info/${studentContribInfoId}/draft")
    .body(StringBody("""{"title" : "info updated shared contrib", "content": "info content updated shared contrib"}"""))
    .check(status.is(200)))
  .exec(http("Info Get updated")
    .get("/actualites/thread/${threadId}/info/${studentContribInfoId}")
    .check(status.is(200),
        jsonPath("$.title").find.is("info updated shared contrib"),
        jsonPath("$.content").find.is("info content updated shared contrib"),
        jsonPath("$.status").find.is("1")
      ))
  // Contributor chooses recipients and submits his piece of news. He can't publish it
  /*.exec(http("Info share read")
    .put("/actualites/thread/${threadId}/info/share/json/${studentContribInfoId}")
    .bodyPart(StringBodyPart("userId", "${teacherId}"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|getInfo"))
    .check(status.is(200)))*/
  .exec(http("Info Submit")
    .put("/actualites/thread/${threadId}/info/${studentContribInfoId}/submit")
    .body(StringBody("""{"title" : "info updated", "owner": {"userId": "${studentId}"}}"""))
    .check(status.is(200)))
  .exec(http("Info try Publish")
    .put("/actualites/thread/${threadId}/info/${studentContribInfoId}/publish")
    .body(StringBody("""{"title" : "info updated", "owner": "${studentId}", "username": "${studentLogin}" }"""))
    .check(status.is(401)))
  .exec(http("Logout 7 - student")
    .get("""/auth/logout""")
    .check(status.is(302)))

  // Teacher publishes the submitted news
  .exec(http("Login 8 - teacher")
    .post("""/auth/login""")
    .formParam("""email""", """${teacherLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))
  .exec(http("Info Publish")
    .put("/actualites/thread/${threadId}/info/${studentContribInfoId}/publish")
    .body(StringBody("""{"title" : "info updated", "owner": "${studentId}", "username": "${studentLogin}" }"""))
    .check(status.is(200)))
  .exec(http("Info Get published (sp)")
    .get("/actualites/thread/${threadId}/info/${studentContribInfoId}")
    .check(status.is(200),
        jsonPath("$.status").find.is("3")
      ))
  .exec(http("Logout 7 - teacher")
    .get("""/auth/logout""")
    .check(status.is(302)))

  .exec(http("Login 8 - student")
    .post("""/auth/login""")
    .formParam("""email""", """${studentLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))
  // A contributor cannot change recipients once the news is published, but he can see them
  .exec(http("Info share read")
    .put("/actualites/thread/${threadId}/info/share/json/${studentContribInfoId}")
    .bodyPart(StringBodyPart("userId", "dummyUserId"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|getInfo"))
    .check(status.is(401)))
  .exec(http("Get share json")
    .get("/actualites/thread/${threadId}/info/share/json/${studentContribInfoId}")
    .check(status.is(200)))

  .exec(http("Delete Info mine (sc)")
    .delete("/actualites/thread/${threadId}/info/${studentContribInfoId}")
    .check(status.is(200)))
   // cant
  .exec(http("Info Update pending (sc)")
    .put("/actualites/thread/${threadId}/info/${infoId}/pending")
    .body(StringBody("""{"title" : "info not updated shared contrib", "content": "info content not updated shared contrib"}"""))
    .check(status.is(401)))
  .exec(http("Info Publish (sc)")
    .put("/actualites/thread/${threadId}/info/${infoId}/publish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(401)))
  .exec(http("Info Unpublish (sc)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unpublish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(401)))
  .exec(http("Info Unsubmit (sc)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unsubmit")
	.body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(401)))
  .exec(http("Thread Update (sc)")
    .put("/actualites/thread/${threadId}")
    .body(StringBody("""{"title" : "thread not updated"}"""))
    .check(status.is(401)))
  .exec(http("Logout 8 - student")
    .get("""/auth/logout""")
    .check(status.is(302)))

  val scnPublish =
  exec(http("Login 9 - teacher")
    .post("""/auth/login""")
    .formParam("""email""", """${teacherLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // The teacher (thread owner) shares publish right with the student on the thread
  .exec(http("Share publish permission with Student as a Person")
    .put("/actualites/thread/share/json/${threadId}")
    .bodyPart(StringBodyPart("userId", "${studentId}"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|createPublished"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|publish"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|unpublish"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|updatePending"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|updatePublished"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|getInfoTimeline"))
    .check(status.is(200)))
  .exec(http("Logout 9 - teacher")
    .get("""/auth/logout""")
    .check(status.is(302)))
  .exec(http("Login 10 - student")
    .post("""/auth/login""")
    .formParam("""email""", """${studentLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // Tests done using a student account who :
  // has publish right on the thread
  // has Read and comment rights on the news
   
  // publication
  .exec(http("Info Update pending (sp)")
    .put("/actualites/thread/${threadId}/info/${infoId}/pending")
    .body(StringBody("""{"title" : "info updated shared publish", "content": "info content updated shared publish"}"""))
    .check(status.is(200)))
  .exec(http("Info Get updated (sp)")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.title").find.is("info updated shared publish"),
        jsonPath("$.content").find.is("info content updated shared publish"),
        jsonPath("$.status").find.is("2")
      ))
  .exec(http("Info Publish (sp)")
    .put("/actualites/thread/${threadId}/info/${infoId}/publish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(200)))
  .exec(http("Info Get published (sp)")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.status").find.is("3")
      ))
  .exec(http("Info Update published (sp)")
    .put("/actualites/thread/${threadId}/info/${infoId}/published")
    .body(StringBody("""{"title" : "info not updated shared publish", "content": "info content not updated shared publis"}"""))
    .check(status.is(200)))
  .exec(http("Info Get updated (sp)")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.title").find.is("info not updated shared publish"),
        jsonPath("$.content").find.is("info content not updated shared publis"),
        jsonPath("$.status").find.is("3")
      ))
  .exec(http("Info Unpublish (sp)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unpublish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(200)))
  .exec(http("Info Get after unpublish (sp)")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.status").find.is("2")
      ))
  .exec(http("Info Unsubmit (sp)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unsubmit")
	.body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(200)))
  // cant
  .exec(http("Thread Update (sp)")
    .put("/actualites/thread/${threadId}")
    .body(StringBody("""{"title" : "thread not updated"}"""))
    .check(status.is(401)))
  .exec(http("Logout 10 - student")
    .get("""/auth/logout""")
    .check(status.is(302)))

  val scnManage =
  exec(http("Login 11 - teacher")
    .post("""/auth/login""")
    .formParam("""email""", """${teacherLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // The teacher (thread owner) shares manage right with the student on the thread
  .exec(http("Share manage permission with Student as a Person")
    .put("/actualites/thread/share/json/${threadId}")
    .bodyPart(StringBodyPart("userId", "${studentId}"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-ThreadController|shareThreadSubmit"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-ThreadController|deleteThread"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-ThreadController|shareThread"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-ThreadController|shareThreadRemove"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-ThreadController|updateThread"))
    .bodyPart(StringBodyPart("actions", "net-atos-entng-actualites-controllers-InfoController|delete"))
    .check(status.is(200)))

  .exec(http("Info Submit")
    .put("/actualites/thread/${threadId}/info/${infoId}/submit")
	.body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(200)))
  .exec(http("Logout 11 - teacher")
    .get("""/auth/logout""")
    .check(status.is(302)))
  .exec(http("Login 12 - student")
    .post("""/auth/login""")
    .formParam("""email""", """${studentLogin}""")
    .formParam("""password""", """blipblop""")
    .check(status.is(302)))

  // Tests done using a student account who :
  // has manage right on the thread
  // has Read and comment rights on the news

  // publication
  .exec(http("Info Publish (sm)")
    .put("/actualites/thread/${threadId}/info/${infoId}/publish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(200)))
  .exec(http("Info Update published (sm)")
    .put("/actualites/thread/${threadId}/info/${infoId}/published")
    .body(StringBody("""{"title" : "info updated shared manager", "content": "info content updated shared manager"}"""))
    .check(status.is(200)))
  .exec(http("Info Get updated (sm)")
    .get("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(200),
        jsonPath("$.title").find.is("info updated shared manager"),
        jsonPath("$.content").find.is("info content updated shared manager"),
        jsonPath("$.status").find.is("3")
      ))
  .exec(http("Info Unpublish (sm)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unpublish")
	.body(StringBody("""{"title" : "info updated", "owner": "${teacherId}", "username": "${teacherLogin}" }"""))
    .check(status.is(200)))
  .exec(http("Info Unsubmit (sm)")
    .put("/actualites/thread/${threadId}/info/${infoId}/unsubmit")
	.body(StringBody("""{"title" : "info updated", "owner": {"userId": "${teacherId}"}}"""))
    .check(status.is(200)))

  // Deletes
  .exec(http("Delete Info (sm)")
    .delete("/actualites/thread/${threadId}/info/${infoId}")
    .check(status.is(401))) // manager cannot delete a draft
  .exec(http("Delete Info (sm)")
    .delete("/actualites/thread/${threadId}/info/${otherInfoId}")
    .check(status.is(200)))
  .exec(http("Delete Thread (sm)")
    .delete("/actualites/thread/${threadId}")
    .check(status.is(200)))
  .exec(http("Get Thread deleted (sm)")
    .get("/actualites/thread/${threadId}")
    .check(status.is(401))) // should filter let pass and return 404 ?
  .exec(http("Logout 12 - student")
    .get("""/auth/logout""")
    .check(status.is(302)))
}
