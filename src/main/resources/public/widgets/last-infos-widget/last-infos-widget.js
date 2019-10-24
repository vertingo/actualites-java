var actualitesWidget = model.widgets.findWidget("last-infos-widget");
actualitesWidget.resultSizeValues = [1,2,3,4,5,6,7,8,9,10];
actualitesWidget.display = {
	edition: false
};

actualitesWidget.updateInfos  = function(){
	http().get('/actualites/infos/last/' + actualitesWidget.resultSize).done(function(infos){
		var enrichedInfos = _.chain(infos).map(function(info){
			info.relativeDate = moment(info.date).fromNow();
			info.tooltip = lang.translate('actualites.widget.thread') + ' : ' + info.thread_title +
				' | ' + lang.translate('actualites.widget.author') + ' : ' + info.username;
			return info;
		}).value();

		actualitesWidget.infos = enrichedInfos;
		model.widgets.apply();
	});
};

if(actualitesWidget.resultSize === undefined){
	http().get('/userbook/preference/maxInfos').done(function(maxInfos){
		if(!maxInfos.preference){
			actualitesWidget.resultSize = 5; // Default size value
		} else {
			actualitesWidget.resultSize = parseInt(maxInfos.preference);
		}
		model.widgets.apply();
		actualitesWidget.updateInfos();
	});
}
else {
	actualitesWidget.updateInfos();
}

actualitesWidget.openConfig = function(){
	actualitesWidget.display.edition = true;
};

actualitesWidget.closeConfig = function(){
	actualitesWidget.display.edition = false;
};

actualitesWidget.saveConfig = function(){
	http().putJson('/userbook/preference/maxInfos', actualitesWidget.resultSize);
	actualitesWidget.closeConfig();
	actualitesWidget.updateInfos();
};
