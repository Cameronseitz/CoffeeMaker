var app = angular.module('myApp');
app.controller('recipeController', function($scope, $http, $q) {

	//ingredients in the backend
	$scope.ingredients = [];
	
	//amount for each ingredient to ADD (new)
	$scope.amounts = [];
	
	$scope.selectedIngredients;
	
	$scope.recipe = {};
	
	$scope.recipe.ingredients = [];
	
	$http.get("/api/v1/inventory").then(function(response) {
		// from https://stackoverflow.com/questions/68168377/angularjs-http-get-response-is-always-object-object
		$scope.inventory = response.data;
		for (var key in $scope.inventory.ingredients) {
			var ingredient = $scope.inventory.ingredients[key];
	    	$scope.ingredients.push(ingredient.name);   
	    }
	});
	
	$scope.addRecipe = function() {
		$scope.success = false;
		$scope.failure = false;
		for (name in $scope.amounts) {
			//only add valid amounts of ingredients
			if ($scope.amounts[name] < 0) {
				console.error("Amounts of ingredient must be positive");
				$scope.failure = true;
				return;
			}
			
			//only add currently selected ingredients
			if ($scope.selectedIngredients[name] == true) {
				let ingredient = {"name": name, "amount": $scope.amounts[name]};
				$scope.recipe.ingredients.push(ingredient);
			}
		}
		
		
		$http.post("/api/v1/recipes", $scope.recipe).then(
				function(success) {
					$scope.success = true;
					$scope.failure = false;
				}, function(rejection) {
					console.error("Error while adding recipe");
					$scope.failure = true;
					$scope.success = false;
				});
	}
	
	 $scope.submit = function() {
	 	$scope.addRecipe();
	 	$scope.reset();
	   
	 }

	
	$scope.reset = function() {
		$scope.recipe = {
			name : '',
			price : '',
			ingredients: []
		};
		$scope.amounts = [];
		$scope.selectedIngredients = {};
		
		if (undefined != $scope.addRecipeForm) {
			$scope.addRecipeForm.$setPristine(); // reset Form
		}
	}
	
	$scope.reset();  
	 

});
