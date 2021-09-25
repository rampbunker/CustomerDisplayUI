package ru.evotor.external.integrations.customer_display;

import ru.evotor.external.integrations.customer_display.ICustomerDisplayClient;

interface ICustomerDisplayService {
    boolean registerClient(ICustomerDisplayClient client);

    void unregisterClient();
}