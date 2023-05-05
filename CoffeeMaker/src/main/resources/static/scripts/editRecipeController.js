var app = angular.module('myApp');

//register controller
app.controller('editRecipeController', function($scope, $http, $q) {

	//recipes in the backend
	$scope.recipesInBackend = [];
	
	//amount for each ingredient to ADD (new)
	$scope.amounts = [];
	
	$scope.recipe = {};
	
	$scope.selectedRecipe = {};
	
	$scope.selectedIngredients;
	
	$scope.ingredientsInBackend = [];
	
	//get inventory and set to scope variable
	$http.get("/api/v1/inventory").then(function(response) {
		// from https://stackoverflow.com/questions/68168377/angularjs-http-get-response-is-always-object-object
		$scope.inventory = response.data;
		
		for (var key in $scope.inventory.ingredients) {
			var ingredient = $scope.inventory.ingredients[key];
	    	$scope.ingredientsInBackend.push(ingredient.name);   
	    }
	});
	
	//get recipes and set to scope variable
	$http.get("/api/v1/recipes").then(function(response) {
		// from https://stackoverflow.com/questions/68168377/angularjs-http-get-response-is-always-object-object
		$scope.recipesInBackend = response.data;
	
		
	});
	
	//scope function for adding the recipe currently in scope
	$scope.addRecipe = function() {
		$scope.success = false;
		$scope.failure = false;
		
		//put in ingredients
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
		
		//set the recipe name
		$scope.recipe.name = $scope.selectedRecipe.name;
		
		//update recipe
		$http.put("/api/v1/recipes/" + $scope.selectedRecipe.name, $scope.recipe).then(
				function(success) {
					$scope.success = true;
					$scope.failure = false;
				}, function(rejection) {
					console.error("Error while editing recipe");
					$scope.failure = true;
					$scope.success = false;
				});
	}
	
	//submits form
	$scope.submit = function() {
		$scope.addRecipe();
	 	$scope.reset();
	}
	 
	//resets UI
	 $scope.reset = function() {
			$scope.recipe = {
				name : '',
				price : '',
				ingredients: []
			};
			$scope.amounts = [];
			$scope.selectedIngredients = {};
			
			if (undefined != $scope.editRecipeForm) {
				$scope.editRecipeForm.$setPristine(); // reset Form
			}
		}
	 
	 $scope.reset();
	 
	 //once recipe is chosen to edit
	 $scope.recipeSelected = function () {
		 try {
			 recipeIngredients = $scope.selectedRecipe.ingredients;
			 //reset the selected ingredients
			 $scope.selectedIngredients = {};
			 
			 //fill selected ingredients with the selected recipe's ingredients
			 for (key in recipeIngredients) {
				 $scope.selectedIngredients[recipeIngredients[key].name] = true;
			 }
			 
			 //autofill price input
			 $scope.recipe.price = $scope.selectedRecipe.price;
			
			 //autofill amount inputs
			 for (key in recipeIngredients) {
				 $scope.amounts[recipeIngredients[key].name] = recipeIngredients[key].amount;
			 }
			 
			 
		 } catch (error) {
			//reset the selected ingredients if "pick one" is chosen
			$scope.selectedIngredients = {};
			 
		 }
		 
	 }
});
