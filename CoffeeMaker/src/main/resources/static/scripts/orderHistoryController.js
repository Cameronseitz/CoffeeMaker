var app = angular.module('myApp');

//register ingredient controller
app.controller('orderHistoryController', function($scope, $http) {
	
	//get the orders
	$http.get("/api/v1/orders/history").then(function(response) {
		$scope.orders = response.data;
	});
	
});