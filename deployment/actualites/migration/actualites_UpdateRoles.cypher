begin transaction
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|view'}) SET a.name = 'net.atos.entng.actualites.controllers.DisplayController|view';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|getThread'}) SET a.name = 'net.atos.entng.actualites.controllers.ThreadController|getThread';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|listThreadInfos'}) SET a.name = 'net.atos.entng.actualites.controllers.ThreadController|listInfosByThreadId';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|updateThread'}) SET a.name = 'net.atos.entng.actualites.controllers.ThreadController|updateThread';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|deleteThread'}) SET a.name = 'net.atos.entng.actualites.controllers.ThreadController|deleteThread';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|shareThread'}) SET a.name = 'net.atos.entng.actualites.controllers.ThreadController|shareThread';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|shareThreadRemove'}) SET a.name = 'net.atos.entng.actualites.controllers.ThreadController|shareThreadRemove';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|shareThreadSubmit'}) SET a.name = 'net.atos.entng.actualites.controllers.ThreadController|shareThreadSubmit';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|getInfo'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|getInfo', a.displayName = 'info.read';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|createDraft'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|createDraft';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|updateDraft'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|updateDraft';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|updatePending'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|updatePending';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|updatePublished'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|updatePublished', a.displayName = 'thread.publish';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|publish'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|publish';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|submit'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|submit';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|unpublish'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|unpublish';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|unsubmit'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|unsubmit';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|trash'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|trash';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|restore'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|restore';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|delete'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|delete';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|comment'}) SET a.name = 'net.atos.entng.actualites.controllers.CommentController|comment', a.displayName = 'info.comment';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|deleteComment'}) SET a.name = 'net.atos.entng.actualites.controllers.CommentController|deleteComment', a.displayName = 'info.comment';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|listThreads'}) SET a.name = 'net.atos.entng.actualites.controllers.ThreadController|listThreads';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|createThread'}) SET a.name = 'net.atos.entng.actualites.controllers.ThreadController|createThread';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|listInfos'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|listInfos';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|listLastPublishedInfos'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|listLastPublishedInfos';
match (a:Action {name: 'net.atos.entng.actualites.controllers.ActualitesController|listInfosForLinker'}) SET a.name = 'net.atos.entng.actualites.controllers.InfoController|listInfosForLinker';
commit