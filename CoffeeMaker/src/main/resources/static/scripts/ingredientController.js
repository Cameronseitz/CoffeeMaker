var app = angular.module('myApp');

//register ingredient controller
app.controller('ingredientController', function($scope, $http) {
	$scope.ingredient = {
		name : '',
		amount : '',
	};

	//function visible in scope which adds the ingredient visible to scope to inventory
	$scope.addIngredient = function() {
		$scope.success = false;
		$scope.failure = false;
		
		//must be integer
		if (!Number.isInteger($scope.ingredient.amount)) {
			$scope.failure = true;
			$scope.reset();
			console.error("Error while adding ingredient");
			return;
		}
		$http.get("/api/v1/inventory").then(function(response) {
			$scope.inventory = response.data
			$scope.inventory.ingredients = $scope.inventory.ingredients.concat($scope.ingredient)
			
			$http.put("/api/v1/inventory/addIngredient", $scope.ingredient).then(
				function(response) {
					$scope.success = true;
					$scope.failure = false;
					$scope.reset();
				}, function(rejection) {
					$scope.success = false;
					$scope.failure = true;
					$scope.reset();
					console.error("Error while adding ingredient");
				}
			);
			
		}, function(rejection) {
			$scope.success = false;
			$scope.failure = true;
			$scope.reset();
			console.error("Error while adding ingredient");
		})
	}

	//submit form by adding ingredient;
	$scope.submit = function() {
		$scope.addIngredient();
	}
	
	//reset form
	$scope.reset = function() {
		$scope.ingredient = {
			name : '',
			amount : ''
		};

		if (undefined != $scope.addIngredientForm) {
			$scope.addIngredientForm.$setPristine(); //reset Form
		}
	}
});