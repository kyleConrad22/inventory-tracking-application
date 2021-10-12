import React from "react";

export default function GatepassForm() {

    function handleSubmit(evt) {

        evt.preventDefault();
        fetch('/api/browser/shipment', {
            method: 'POST',
            body: new FormData(evt.target)
        }).then(
            (response) => {
                if (response.ok) {
                    alert("Shipment create successful!")
                } else {
                    alert("Something went wrong while attempting to create shipment!")
                }
            }
        ).catch(
            (error) => {
                alert(error)
            }
        )
        evt.target.reset()
        return false
    }

    return (
        <form onSubmit={handleSubmit}>
            <input id='time-in' name='timeIn' type='text' placeholder='Enter Time In:'/>
            &nbsp;&nbsp;&nbsp;
            <input id='driver-name' name='driverName' type='text' placeholder='Enter Driver Name:' />
            &nbsp;&nbsp;&nbsp;
            <input id='license' name='license' type='text' placeholder='Enter License Plate Number:' />
            &nbsp;&nbsp;&nbsp;
            <input id='trucking-company' name='truckingCompany' type='text' placeholder='Enter Trucking Company:' />
            &nbsp;&nbsp;&nbsp;
            <input id='gatepass-number' name='gatepassNumber'type='text' placeholder='Enter Gatepasss Number:' />
            &nbsp;&nbsp;&nbsp;
            <input id='dor' name='dor' type='text' placeholder="Enter Pickup Number:" />
            &nbsp;&nbsp;&nbsp;
            <input id='file' name='file' type='file' />
            &nbsp;&nbsp;&nbsp;
            <button id='submit' type='submit'>Run TC3 Automation</button>
        </form>
    )
}