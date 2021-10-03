import React from "react";
import ToBeImplemented from "../../util/to_be_implemented";

export default function RusalUpdateItem() {

    function UpdateFields() {
        return (
            <form>
                <input id="heatNum" name='heatNum' type='text' placeholder='Enter Bundle Heat Number:' />
                &nbsp;&nbsp;&nbsp;
                <input id='workOrder' name='workOrder' type='text' placeholder='Enter Work Order:' />
                &nbsp;&nbsp;&nbsp;
                <input id='loadNum' name='loadNum' type='text' placeholder='Enter Load Number:' />
                &nbsp;&nbsp;&nbsp;
                <input id='loader' name='loader' type='text' placeholder='Enter Loader Name:' />
                &nbsp;&nbsp;&nbsp;
                <input id='loadTime' name='loadTime' type='text' placeholder='Enter Load Time:' />
                &nbsp;&nbsp;&nbsp;
                <input id='vessel' name='vessel' type='text' placeholder='Enter Vessel Name:' />
                &nbsp;&nbsp;&nbsp;
                <input id='checker' name='checker' type='text' placeholder='Enter Checker Name:' />
                &nbsp;&nbsp;&nbsp;
                <button id='submit' type='submit'>Submit</button>
            </form>
        );
    }

    return (
        <div id='update-rusal'>
            <h3>Update Rusal Item</h3>
            <ToBeImplemented />
            <UpdateFields />
        </div>
    );
}