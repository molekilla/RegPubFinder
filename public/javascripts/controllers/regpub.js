App.Controllers.Fichas = Backbone.Router.extend({
	routes: {
		"sa/:name": "findByName",
		"search/:query/:pageIndex": "searchPage",
		"search/:query": "search"
	},
	findByName: function(name) {
		new App.Views.Index({ key: name });
	},
	search: function(query) {
		this.searchPage(query, 1);
	},
	searchPage: function(query, pageIndex) {
		var searchResults = new App.Collections.SearchResults();
		searchResults.query = query;
		searchResults.pagePosition = pageIndex;
		searchResults.fetch({
			success: function() {
				new App.Views.SearchView({ items: searchResults });
			},
			error: function() {
				new App.Views.SearchView({ error: true });
			}
		});
	}
});