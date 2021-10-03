import React from "react";
import ToBeImplemented from "../../util/to_be_implemented";

export default function RusalDownloads() {

    function DownloadDatabase() {
        return(
            <button id='download-database' type='button'>Download Full Database</button>
        );
    }

    function DownloadByOrderAndLoad() {
        return (
            <form>
                <input id='work-order' name='workOrder' type='text' placeholder='Enter Work Order:'/>
                &nbsp;&nbsp;&nbsp;
                <input id='load-number' name='loadNum' type='text' placeholder='Enter Load Number:'/>
                &nbsp;&nbsp;&nbsp;
                <button id='download-order-and-load' type='button'>Download By Order and Load</button>
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