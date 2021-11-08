import React from "react";

import AddLot from "./components/add_lot";
import AddMark from "./components/add_mark";

export default function RusalUpdateItem() {

    return (
        <div id='update-rusal'>
            <h3>Update Rusal Items</h3>
            &nbsp;&nbsp;&nbsp;
            <AddMark />
            &nbsp;&nbsp;&nbsp;
            <AddLot />
        </div>
    )
}