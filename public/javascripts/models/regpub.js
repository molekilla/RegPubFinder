// Ficha Model
var Ficha = Backbone.Model.extend({
	defaults: {
		"domicilio": "",
		"directores": [],
		"subscriptores": [],
		"estado": "",
		"fechaRegistro": "",
		"ficha": "",
		"nombre": "",
		"tomo": "",
		"documento": "",
		"asiento": "",
		"microRollo": "",
		"folio": ""
	},
	initialize: function() {
		
	}
});

App.Collections.Fichas = Backbone.Collection.extend({
	model: Ficha,
	id: null,
	url: function() {
		return "/findByName/" + this.id;
	}
});

// Search model
var Search = Backbone.Model.extend({
	initialize: function() {
		
	}
});

App.Collections.SearchResults = Backbone.Collection.extend({
	model: Search,
	query: null,
	pagePosition: 1,
	url: function() {
		return "/search/AND/" + this.query + "/" + this.pagePosition;
	}
});