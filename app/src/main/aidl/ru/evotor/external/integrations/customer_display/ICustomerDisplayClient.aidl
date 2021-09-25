package ru.evotor.external.integrations.customer_display;

// Declare any non-default types here with import statements

interface ICustomerDisplayClient {
    void clearCustomerDisplay();

    void stringOutputOnCustomerDisplay(String data);

    Bundle getOutputOptions();
}