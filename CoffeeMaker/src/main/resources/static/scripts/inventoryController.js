var app = angular.module('myApp');
app.controller('inventoryController', function($scope, $http) {
	
	//amount for each ingredient to ADD (new). array of ints
	$scope.amounts = [];
	
	//inventory that is being displayed
	$scope.inventory = {
		ingredients: []
	};
	
	$http.get("/api/v1/inventory").then(function(response) {
		$scope.inventory = response.data;
	});
	
	$scope.updateInventory = function() {
		$scope.success = false;
		$scope.failure = false;
		
		//update inventory
		for (let i = 0; i < $scope.amounts.length; i++) {
			$scope.inventory.ingredients[i].amount += $scope.amounts[i];
		}
		
		//create inventory that will be put to backend
		const inventoryCopy = JSON.parse(JSON.stringify($scope.inventory));
		for (let i = 0; i < $scope.amounts.length; i++) {
			inventoryCopy.ingredients[i].amount = $scope.amounts[i];
		}
		
		console.log(inventoryCopy)
		console.log($scope.inventory)
		$http.put("/api/v1/inventory/", inventoryCopy).then(
				function(success) {
					$scope.success = true;
					$scope.failure = false;
				}, function(rejection) {
					console.error("Error while adding ingredients");
					$scope.failure = true;
					$scope.success = false;
				});
	}
	
	 $scope.submit = function() {
	 	$scope.updateInventory();
	 	$scope.reset();
	 }
	 
	 $scope.reset = function() {
			if (undefined != $scope.updateInventoryForm) {
				$scope.updateInventoryForm.$setPristine(); // reset Form
			}
			$scope.amounts = [];
		}
	 
	$scope.reset();  

});
