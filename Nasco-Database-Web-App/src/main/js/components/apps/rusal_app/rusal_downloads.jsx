import React, { useEffect } from "react";
import ToBeImplemented from "../../util/to_be_implemented";

export default function RusalDownloads() {

    function DownloadDatabase() {

        return(
            <a href='/api/rusal/excel/download-all' download>Download Full Database</a>
        );
    }

    function DownloadByOrderAndLoad() {

        function httpDownloadRequest(evt) {
            evt.preventDefault();

            let params = new URLSearchParams();
            let input = new FormData(evt.target)

            params.set('workOrder', input.get('workOrder'))
            params.set('loadNum', input.get('loadNum'))
            
            fetch(`/api/rusal/excel/download-by-order-and-load?${params}`)
            .then(
                (response) => {
                    if (response.ok) {
                        return response;
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
            return false
        }

        function handleSubmit(evt) {
            httpDownloadRequest(evt)
            .then(
                (response) => response.blob()
            ).then(
                (blob) => {
                    const url = window.URL.createObjectURL(new Blob([blob]));
                    const link = document.createElement('a');
                    link.href = url;
                    link.setAttribute('download', `sample.${file}`);

                    document.body.appendChild(link);

                    link.click();
                    
                    link.parentNode.removeChild(link);
                }
            )
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