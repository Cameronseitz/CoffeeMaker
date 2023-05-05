var app = angular.module('myApp');

//register ingredient controller
app.controller('currentOrdersCustomerController', function($scope, $http) {

	//order is clicked
	$scope.selectOrder = function(order) {
		//double click removes selection
		if ($scope.selectedOrder === order) {
	    	$scope.selectedOrder = null;
	    
	    //order must be ready to be able to be selected
	    } else if (order.status != "Ready") {
			return;
	
		// otherwise select the order
		} else {
	    	$scope.selectedOrder = order;
	    }
	};
	
	//if the user is logged in get the username of logged in user
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
	
	
	//get the orders that belong to singed in user and are not already picked up
	$http.get("/api/v1/orders").then(function(response) {
		$scope.orders = response.data.filter(function(order) {
        	return (order.owner === $scope.user.username && order.status != "Picked Up");
    	});
	});
	
	//upon button click, update the order
	$scope.pickUpOrder = function() {
		$scope.success = false;
		$scope.failure = false;
		
		//no order selected
		if (!$scope.selectedOrder) {
			return;
		}
		
		//update order
		$scope.selectedOrder.status = "Picked Up";
		//make put command with this updated order
	    $http.put('/api/v1/orders/' + $scope.selectedOrder.id, $scope.selectedOrder).then(function(response) {
			console.log('Order updated successfully:', response.data);
			
			// Find the index of the order to remove
			let index = $scope.orders.findIndex(order => order.id === $scope.selectedOrder.id);
		
			// If the index is found, remove the order
			if (index !== -1) {
			  $scope.orders.splice(index, 1);
			}
			
			//remove selection
			$scope.selectedOrder = null;
			
			$scope.success = true;
			$scope.failure = false;
		}, function(error) {
		    console.error('Error updating order:', error);
		    $scope.success = false;
			$scope.failure = true;
		});
	};

});