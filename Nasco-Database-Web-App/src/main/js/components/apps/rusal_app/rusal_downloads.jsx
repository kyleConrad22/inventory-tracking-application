import React, { useEffect } from "react";
import ToBeImplemented from "../../util/to_be_implemented";

export default function RusalDownloads() {

    function DownloadDatabase() {

        function handleClick() {
            fetch("/api/rusal/excel/download-all")
            .then(
                (response) => {
                    if (response.ok) {
                        alert("Downloaded Database Copy")
                    } else {
                        alert("Database Download Failed")
                    }
                }).catch(
                    (error) => {
                        alert(error)
                })
            return false;
        }

        return(
            <button id='download-database' type='button' onClick={ handleClick }>Download Full Database</button>
        );
    }

    function DownloadByOrderAndLoad() {

        function handleSubmit(evt) {
            evt.preventDefault();

            let params = new URLSearchParams();
            let input = new FormData(evt.target)

            params.set('workOrder', input.get('workOrder'))
            params.set('loadNum', input.get('loadNum'))
            
            fetch(`/api/rusal/excel/download-by-order-and-load?${params}`)
            .then(
                (response) => {
                    if (response.ok) {
                        alert("Downloaded Tally Sheet")
                    } else {
                        alert("Tally Sheet Download Failed")
                    }
                }
            ).catch(
                (error) => {
                    alert(error)
            })
            evt.target.reset();
            return false;
        }

        return (
            <form onSubmit={ handleSubmit }>
                <input id='work-order' name='workOrder' type='text' placeholder='Enter Work Order:'/>
                &nbsp;&nbsp;&nbsp;
                <input id='load-number' name='loadNum' type='text' placeholder='Enter Load Number:'/>
                &nbsp;&nbsp;&nbsp;
                <button id='download-order-and-load' type='submit'>Download By Order and Load</button>
            </form>
        );
    }

    return (
        <div id='downloads'>
            <h3>Download Links</h3>
            <ToBeImplemented />
            <DownloadDatabase />
            <DownloadByOrderAndLoad />
        </div>
    );
}