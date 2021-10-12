import React from "react";

import AlgomaReception from "./receptions/algoma/alogoma_reception";
import ShipmentAutomation from "./shipments/shipment_automation";

export default function BrowserAutomation() {

    return (
        <div>
            <h2>Browser Automation</h2>
            <AlgomaReception />
            <ShipmentAutomation />
        </div>
    );
}