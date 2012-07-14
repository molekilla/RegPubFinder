App.Controllers.TypeaheadItems = Backbone.Router.extend({
	routes: {
		"": "index",
	},
	index: function() {
		new App.Views.Index({ items: new App.Collections.TypeaheadItems() });
	}
});