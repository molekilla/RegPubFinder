App.Views.Index = Backbone.View.extend({
	tagName: "input",
	className: "search-query",
	initialize: function() {
		this.items = this.options.items;
		this.key = this.options.key;
		this.autocomplete = $('.search-query').typeahead();
		this.autocomplete.source = [];
		this.autocomplete.on('keyup', this, this.typeaheadLookup);
		if ( this.key != null )
	    {
			this.loadDocumentView(this.key);
		}
		this.render();
	},
	autocomplete: null,
	findRegpubItem: function(obj) {
		var self = obj.data;
		self.loadDocumentView(obj.srcElement.innerText);
	},
	loadDocumentView: function(id) {
		var fichas = new App.Collections.Fichas();
		fichas.id = id;
		fichas.fetch({
			success: function() {
				new App.Views.DocumentView({ items: fichas });
			},
			error: function() {
				new App.Views.DocumentView({ error: true });
			}
		});
	},
	typeaheadLookup: function(e) {
		var self = $(this);

		if( !self.data('active') && self.val().length > 0) {
			self.data("active", true);
			var view = e.data;
			var searchResults = new App.Collections.TypeaheadItems();
			searchResults.value = self.val();
			searchResults.fetch({
				success: function () {
					self.data('typeahead').source = searchResults.toArray();
					self.trigger('keyup');
					$("ul.typeahead.dropdown-menu").on("click", view, view.findRegpubItem)
					self.data("active", false);
				} ,
				error: function() {
					self.data("active", false);
				}		
			});
		}
	},
	render: function() {
		if ( this.items != null  )
		{
			this.autocomplete.source = this.items;
		}
		return this;
	}
})