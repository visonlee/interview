package com.lws.interview.questions.abstraction;


import java.util.Arrays;
import java.util.List;

class Request {}
class Response {}

interface ValidateRule<T> {
    default boolean validate(T request) {return true;}
}

class IPValidateRule implements ValidateRule<Request> {
    @Override
    public boolean validate(Request request) {
        // check is valid IP address
        return false;
    }
}

class InvestmentAccountValidateRule implements ValidateRule<Request> {
    @Override
    public boolean validate(Request request) {
        // check account status
        return false;
    }
}

class ProductValidateRule implements ValidateRule<Request> {
    @Override
    public boolean validate(Request request) {
        // check if is a valid product
        return false;
    }
}


public class PreTradeCheck {


    public Response createOrder(Request request) {

        List<ValidateRule> rules = Arrays.asList(/*validate rules*/); // put all the rule in config files

        rules.forEach(v -> {
            if (!v.validate(request)) {
                //put you error handling here
                return;
            }
        });

        // call create order api
        return new Response();
        //
    }


}
