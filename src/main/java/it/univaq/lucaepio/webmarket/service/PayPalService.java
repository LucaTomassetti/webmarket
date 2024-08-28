/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author lucat
 */

public class PayPalService {
    private PayPalHttpClient client;
    private String baseUrl;

    public PayPalService(String baseUrl) throws Exception {
        this.baseUrl = baseUrl;
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("paypal.properties")) {
            if (input == null) {
                throw new Exception("Unable to find paypal.properties");
            }
            prop.load(input);
        }

        String clientId = prop.getProperty("paypal.client.id");
        String clientSecret = prop.getProperty("paypal.client.secret");

        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            throw new Exception("PayPal credentials not found in paypal.properties");
        }

        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        client = new PayPalHttpClient(environment);
    }

    public String createOrder() throws IOException {
        OrdersCreateRequest request = new OrdersCreateRequest();
        request.prefer("return=representation");
        request.requestBody(buildRequestBody());

        HttpResponse<Order> response = client.execute(request);
        Order order = response.result();

        return order.id();
    }

    private OrderRequest buildRequestBody() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext()
            .returnUrl(baseUrl + "/paypalConfirm")
            .cancelUrl(baseUrl + "/paypalCancel");
        orderRequest.applicationContext(applicationContext);

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(new AmountWithBreakdown().currencyCode("EUR").value("20.00"));
        purchaseUnits.add(purchaseUnitRequest);
        orderRequest.purchaseUnits(purchaseUnits);

        return orderRequest;
    }

    public boolean captureOrder(String orderId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        HttpResponse<Order> response = client.execute(request);
        return response.statusCode() == 201;
    }
    public String getApproveUrl(String orderId) throws IOException {
        OrdersGetRequest request = new OrdersGetRequest(orderId);
        HttpResponse<Order> response = client.execute(request);
        Order order = response.result();

        for (LinkDescription link : order.links()) {
            if ("approve".equals(link.rel())) {
                return link.href();
            }
        }

        throw new IOException("Approve URL not found in PayPal order response");
    }
}
