var App = {
		Views: {},
		Controllers: {},
		Collections: {},
		init: function() {
			new App.Controllers.TypeaheadItems();
			new App.Controllers.Fichas();
			Backbone.history.start({pushState: false});
		}
};
