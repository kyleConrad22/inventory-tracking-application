import React from "react";

import ToBeImplemented from "../../core/to_be_implemented";
import AddBarge from "./components/add_barge";
import AddLot from "./components/add_lot";
import AddMark from "./components/add_mark";

export default function RusalUpdateItem() {

    function UpdateFields() {

        function handleSubmit(evt) {
            evt.preventDefault()
            
            fetch("/api/rusal/update", {
                method: 'POST',
                body: new FormData(evt.target)
            }).then(
                (response) => {
                    if (response.ok) {
                        alert("Update successful")
                    } else {
                        alert("Update failed")
                    }
                }
            ).catch(
                (error) => {
                    alert(error);
                }
            )

            evt.target.reset();
            return false;
        }

        return (
            <form onSubmit={ handleSubmit }>
                <input id="heat-number" name='heatNum' type='text' placeholder='Enter Bundle Heat Number:' />
                &nbsp;&nbsp;&nbsp;
                <input id='work-order' name='workOrder' type='text' placeholder='Enter Work Order:' />
                &nbsp;&nbsp;&nbsp;
                <input id='load-number' name='loadNum' type='text' placeholder='Enter Load Number:' />
                &nbsp;&nbsp;&nbsp;
                <input id='loader' name='loader' type='text' placeholder='Enter Loader Name:' />
                &nbsp;&nbsp;&nbsp;
                <input id='load-time' name='loadTime' type='text' placeholder='Enter Load Time:' />
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
            <UpdateFields />
            &nbsp;&nbsp;&nbsp;
            <AddMark />
            &nbsp;&nbsp;&nbsp;
            <AddBarge />
            &nbsp;&nbsp;&nbsp;
            <AddLot />
        </div>
    );
}