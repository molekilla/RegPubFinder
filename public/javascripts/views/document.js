App.Views.DocumentView = Backbone.View.extend({
	tagName: "div",
	el: $("#searchResults"),
	initialize: function() {
		this.items = this.options.items;
		if ( this.options.error != null )
		{
			this.errorMessage = "No records found";
		}
		this.render();
	},
	render: function() {
		var parentData = this.items.models[0].toJSON();
		var data = parentData.document;
		data.stats = parentData.stats;
		data.dignatarios.stats = data.stats;
		data.directores.stats = data.stats;
		data.subscriptores.stats = data.stats;
		
		var byNameUrl = "sa/" + data.nombre;
		var byFichaUrl = "ficha/" + data.ficha;
		Backbone.history.navigate(byNameUrl, false);

		Handlebars.registerHelper("relCount", function(stats, name) {
			try { 
			    var foundItem = stats[name];
			    return new Handlebars.SafeString(foundItem.count);
			}
			catch (err) {
				return new Handlebars.SafeString("1");
			}
		});
		var source   = $("#fichaTemplate").html();
		var template = Handlebars.compile(source);
		
		
		if (this.errorMessage == null )
		{
			$(this.el).html(template({ models: data }));
		} else {
			$(this.el).html(this.errorMessage);
		}
		
		return this;
	}
})