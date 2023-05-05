var app = angular.module('myApp');

//register the controller
app.controller('deleteRecipeController', function($scope, $http) {
	
	//function to update the given recipe
	function updateRecipes() {
		$http.get("/api/v1/recipes").then(function (response) {
			$scope.recipes = response.data;
		});
	}
	
	//function that calls api to delete a recipe
	function deleteRecipe(recipe) {
        $http.delete("/api/v1/recipes/" + recipe)
            .then(
            function (response) {
            		console.log(response);
    				$scope.submissionSuccess=true;	
    				
                
             	updateRecipes();
            },
            function(rejection){
                console.error('Error while deleting recipe');
                console.log(rejection);
                
				$scope.submissionSuccess=false;	
                
             	// Update recipe list
		        $http.get("/api/v1/recipes").then(function (response) {
	  				$scope.recipes = response.data;
	  			});
            }
        );
	}
	
	//function available in scope that deletes the recipe in scope
	$scope.del = function(){				
		if ($scope.deleteAll) {
			for (var i = 0, len = $scope.recipes.length; i < len; i++) {
				var recipe = $scope.recipes[i];
				deleteRecipe(recipe.name);
			}
		} else {
			deleteRecipe($scope.name);
		}
		
        updateRecipes();
	}
	
	updateRecipes();
});