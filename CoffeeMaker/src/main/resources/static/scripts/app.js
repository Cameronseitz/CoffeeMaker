var app = angular.module('myApp', ["ngRoute"]);

//configuration of front end angular routing
app.config(['$routeProvider', function($routeProvider) {
    $routeProvider
    .when("/", {
        templateUrl : "home.html",
		controller: "homeController"
    })
    .when("/inventory", {
        templateUrl : "inventory.html",
        controller: "inventoryController"
    })
    .when("/recipe", {
        templateUrl : "recipe.html",
		controller: "recipeController"
    })
    .when("/editrecipe", {
        templateUrl : "editrecipe.html",
		controller: "editRecipeController"
    })
    .when("/ingredient", {
        templateUrl : "ingredient.html",
        controller: "ingredientController"
    })
    .when("/currentorders", {
        templateUrl : "currentorders.html",
        controller : "currentOrdersController"
    })
    .when("/deleterecipe", {
        templateUrl : "deleterecipe.html",
		controller: "deleteRecipeController"
    })
    .when("/ordercoffee", {
        templateUrl: "ordercoffee.html",
		controller: "orderCoffeeController"
    })
    .when("/currentorders-customer", {
        templateUrl : "currentorders-customer.html",
        controller : "currentOrdersCustomerController"
    })
    .when("/orderhistory", {
        templateUrl : "orderhistory.html",
        controller : "orderHistoryController"
    })
    .otherwise({
        redirectTo: '/home'
    });
}]);

//register the controller for index.html
app.controller('indexController', function($scope, $http, $q) {
	
	//if the user is logged in set the username and role of the user in a variable
	//otherwise redirect to the login page
	$http.get("/api/v1/users").then(
			function(success) {
				$scope.user = {
					username: success.data.username,
					role: success.data.role
				}
			},
			function(failure) {
				window.location.href = "/login";
			}
		);
	
	//if customer return true, otherwise false
	$scope.isCustomer = function() {
		return $scope.user != undefined && $scope.user.role == 'customer';
	}
	
	//if employee return true, otherwise false
	$scope.isEmployee = function() {
		return $scope.user != undefined && $scope.user.role == 'employee';
	}
	
	//log the user out of the system and redirect to login page
	$scope.logout = function() {
		$http.put("api/v1/users/" + $scope.user.username).then(
				function(success) {
					window.location.href = "/login";
				},
				function(failure) {
					console.log("failed to logout.")
				}
			);
	}
});
