
var TypeAheadItem = Backbone.Model.extend({
	defaults: {
		"item": ""
	},
	initialize: function(){
	}
});

App.Collections.TypeaheadItems = Backbone.Collection.extend({
	model: TypeAheadItem,
	value: null,
	url: function() {
		return "/typeahead/" + this.value;
	},
	toArray: function() {
		return _(this.models).map( function (i) { return i.get("item"); } );
	}
});