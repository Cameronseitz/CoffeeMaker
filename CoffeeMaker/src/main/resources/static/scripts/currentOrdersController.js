var app = angular.module('myApp');

//register ingredient controller
app.controller('currentOrdersController', function($scope, $http) {
	
	//order is clicked
	$scope.selectOrder = function(order) {
		//double click removes selection
		if ($scope.selectedOrder === order) {
	    	$scope.selectedOrder = null;
	    // otherwise selects the order
	    } else {
	    	$scope.selectedOrder = order;
	    }
	};
	
	//get the orders
	$http.get("/api/v1/orders").then(function(response) {
		$scope.orders = response.data.filter(function(order) {
        	return order.status === "In Progress";
    	});
	});
	
	
	//upon button click, update the order
	$scope.finishOrder = function() {
		$scope.success = false;
		$scope.failure = false;
		
		//no order is selected
		if (!$scope.selectedOrder) {
			return;
		}
		
		//update order
		$scope.selectedOrder.status = "Ready";
				
		//put with this updated order
	    $http.put('/api/v1/orders/' + $scope.selectedOrder.id, $scope.selectedOrder).then(
			//successful put
			function(success) {
				
				// Find the index of the order to remove
				let index = $scope.orders.findIndex(order => order.id === $scope.selectedOrder.id);
			
				// If the index is found, remove the order
				if (index !== -1) {
				  $scope.orders.splice(index, 1);
				}
				
				//remove selector
				$scope.selectedOrder = null;
				
				$scope.success = true;
				$scope.failure = false;
			
			//error while doing put
			}, function(error) {
				if (error.data.message == "Cancelled") {
					$scope.selectedOrder.status = "Cancelled";
				}
			    
			    // Find the index of the order to remove
				let index = $scope.orders.findIndex(order => order.id === $scope.selectedOrder.id);
			
				// If the index is found, remove the order
				if (index !== -1) {
				  $scope.orders.splice(index, 1);
				}
				
				//remove selector
				$scope.selectedOrder = null;
				
				$scope.success = false;
				$scope.failure = true;
			});
	};
	
});