var app = angular.module('myApp');
app.controller('orderCoffeeController', function($scope, $http) {
	$http.get("/api/v1/recipes").then(function(response) {
		$scope.recipes = response.data;
	});
	
	/*
	 This is a list of items with the following format:
		{
			recipe: [this is a recipe object],
			qty: [this is a number],
			cost: [this should be set to recipe.price * qty]
		}
	*/
	$scope.orderItems = [];
	
	$scope.orderTotalCost = 0;
  
	// function to calculate the total
	function calculateTotal() {
		$scope.orderTotalCost = 0;
    
		for (let i = 0; i < $scope.orderItems.length; i++) {
			$scope.orderTotalCost += $scope.orderItems[i].cost;
		}
  	}
  
	// watch for changes to the items array and recalculate the total
	// second param is set to true deep watch to monitor changes to array elements
	$scope.$watch('orderItems', function(newValue, oldValue) {
	    if (newValue !== oldValue) {
	      calculateTotal();
	    }
	}, true);
	
  	// calculate the total initally
	calculateTotal();
	
	//adds a beverage to the order items
	$scope.addBeverageToOrder = function(beverage) {
		const alreadyInOrderBeverage = $scope.orderItems.find((x) => x.recipe.name == beverage.name);
		
		//if the beverage is not already in the order add it
		if (alreadyInOrderBeverage == undefined) {
			$scope.orderItems.push(
				{
					recipe: beverage,
					qty: 1,
					//this is a getter function which creates a dynamic readonly property
					get cost() {
    					return this.recipe.price * this.qty;
  					}
				}
			);
		} else { //otherwise increase the qty of the beverage already in the order
			alreadyInOrderBeverage.qty++;
		}
	}
	
	$scope.removeOrderItem = function(orderItem) {
		$scope.orderItems = $scope.orderItems.filter(
			x => x.recipe.name != orderItem.recipe.name
		)
	}
	
	//determines whether the conditions are met for the user to place the order
	$scope.canPlaceOrder = function() {
		//userPaymentAmount variable defined in html
		return $scope.orderItems.length != 0 && $scope.userPaymentAmount >= $scope.orderTotalCost
	}
	
	//this should be called when the user wishes to place their order
	$scope.placeOrder = function() {
		//get owner of user
		$http.get("/api/v1/users").then(
			function(success) {
				$scope.user = {
					username: success.data.username,
				}
			},
			function(failure) {
				$scope.success = false;
				$scope.failure = true;
				return;
			}
		)
		
		/*
		 if order items has a "Black Coffee" with quantity 2 and "Iced Latte" with qty 1,
		 then this array will have ["Black Coffee", "Black Coffee", "Iced Latte"], except
		 that instead of just being the name it is a recipe
	     */
		const recipesInOrder = [];
		$scope.orderItems.forEach(orderItem => {
			for (let i = 0; i < orderItem.qty; i++) {
				recipesInOrder.push(orderItem.recipe);
			}
		});
		
		//construct order object
		const order = { 
			owner: $scope.user.username,
			status: 'In Progress',
			beverages: recipesInOrder,
			cost: $scope.orderTotalCost
		}

		$http.post("/api/v1/orders/", order).then(
				function() {
					$scope.failure = false;
					$scope.success = true;
					$scope.resetForm();
				}, function() {
					$scope.success = false;
					$scope.failure = true;
				});
	}
	
	$scope.resetForm = function() {
		$scope.orderItems = [];
		$scope.userPaymentAmount = undefined;
	}
});