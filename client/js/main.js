var api = 'http://0.0.0.0:8080';
var key = '3cf0e880-a782-4ce6-a63c-7ae95891051f';


function ajaxRequest(method, url) {
  console.log(url);
  console.log(method);
  $('.message-block').hide("slide");
  $.ajax({
    url: url,
    dataType: 'text',
    type: method,
    success: function(data) {
      $('.message-block').show("slide");
      $('.message').text(data);
    },
    error: function(xhr, status, error) {
      $('.message-block').show("slide");
      $('.message').text(error);
    }
  });
}

$("#get-customers").click(function(e) {

  var params = {
    api_key: key
  };

  ajaxRequest('GET', api + '/api/customer?' + jQuery.param(params));

});

$("#add-account-type").click(function(e) {
  var account = $('#account-number').val();
  var option = $("#new-account-select").val();

  console.log(account);
  console.log(option);

  var params = {
    account: account,
    api_key: key,
    account_type: option
  };

  ajaxRequest('POST', api + '/api/customer/add-account?' + jQuery.param(params));
});


$("#search-customer").click(function(e) {
  var id = $('#customer-id').val();
  var params = {
    api_key: key
  };

  ajaxRequest('GET', api + '/api/customer/' + id + '?' + jQuery.param(params));

});

$("#delete-customer").click(function(e) {
  var id = $('#remove-id').val();
  var params = {
    api_key: key
  };

  ajaxRequest('GET', api + '/api/customer/delete/' + id + '?' + jQuery.param(params));

});

$("#get-transactions").click(function(e) {
  var id = $('#customer_id').val();
  var params = {
    api_key: key
  };

  ajaxRequest('GET', api + '/api/transaction/' + id + '?' + jQuery.param(params));

});


$("#transfer").click(function(e) {
  var from = $('#from').val();
  var to = $('#to').val();
  var amount = $('#amount').val();

  var params = {
    from: from,
    to: to,
    amount: amount,
    api_key: key
  };

  ajaxRequest('POST', api + '/api/transfer/create?' + jQuery.param(params));
});


$("#get-balance").click(function(e) {
  var account = $('#account').val();

  var params = {
    account_number: account,
    api_key: key
  };

  ajaxRequest('GET', api + '/api/balance?' + jQuery.param(params));
});


$("#withdrawl").click(function(e) {
  var account = $('#account_number').val();
  var amount = $('#amount').val();

  var params = {
    account: account,
    amount: amount,
    api_key: key
  };

  ajaxRequest('POST', api + '/api/withdrawl?' + jQuery.param(params));
});


$("#add-customer").click(function(e) {
  var customer_name = $('#name').val();
  var email = $('#email').val();
  var password = $('#password').val();
  var address = $('#address-input').val();
  var option = $("#custom-select").val();



  var params = {
    name: customer_name,
    email: email,
    address: address,
    password: password,
    account_type: option,
    api_key: key
  };

  ajaxRequest('POST', api + '/api/customer/create?' + jQuery.param(params));


});

$("#lodgement").click(function(e) {
  var account = $('#account_number').val();
  var amount = $('#amount').val();

  var params = {
    account: account,
    amount: amount,
    api_key: key
  };

  ajaxRequest('POST', api + '/api/lodgement?' + jQuery.param(params));
});