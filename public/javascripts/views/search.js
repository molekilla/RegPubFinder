App.Views.SearchView = Backbone.View.extend({
	tagName: "div",
	el: $("#searchResults"),
	initialize: function() {
		$('.search-query').val("")
		this.items = this.options.items;
		if ( this.options.error != null )
		{
			this.errorMessage = "No records found";
		}
		this.render();
	},
	render: function() {
		var parentData = this.items.models[0].toJSON();
		parentData.pages = Math.round(parentData.total / 20);
		parentData.index = this.items.pagePosition;
		parentData.name = this.items.query;

		Handlebars.registerHelper("pages", function(model) {
			try { 
				var isLastPage = model.hits.length < 20;
				var start = 1;
				var end = 10;

				start = model.index - 4;
				if ( start < 1 )
				{
					start = 1;
				}
				end = start + 9;

				var output = "";
				if ( model.pages > 1 ) {
					var pageEl = "<li{3}><a href='#search/{0}/{1}'>{2}</a></li>";
					for ( var i=start;i<=end;i++)
					{
						if ( model.index == i )
						{
							// active
							output += pageEl.replace("{0}", model.name)
							.replace("{1}", i)
							.replace("{2}", i)
							.replace("{3}", " class='active'")

						} else if (model.index < i && isLastPage ) {
							// do nothing
						}
						else {
							// normal
							output += pageEl.replace("{0}", model.name)
							.replace("{1}", i)
							.replace("{2}", i)
							.replace("{3}", "")
						}
					}
				}
				return new Handlebars.SafeString(output);
			}
			catch (err) {
				return new Handlebars.SafeString("1");
			}
		});
		var source   = $("#searchTemplate").html();
		var template = Handlebars.compile(source);

		if (this.errorMessage == null )
		{
			$(this.el).html(template({ result: parentData }));
		} else {
			$(this.el).html(this.errorMessage);
		}

		return this;
	}
})