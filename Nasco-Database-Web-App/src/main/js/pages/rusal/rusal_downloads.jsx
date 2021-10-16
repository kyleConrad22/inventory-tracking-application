import React from "react";

export default function RusalDownloads() {

    function DownloadDatabase() {

        return(
            <a href='/api/rusal/excel/download-all' download>Download Full Database</a>
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
                (response) => response.blob()
            ).then((blob) => {
                const url = window.URL.createObjectURL(new Blob([blob]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', `rusal-order-${input.get('workOrder')}-load-${input.get('loadNum')}.xlsx`);

                document.body.appendChild(link);

                link.click();

                link.parentNode.removeChild(link);
            })
            .catch(
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

    function DownloadByBarge() {

        function handleSubmit(evt) {
            evt.preventDefault();

            let params = new URLSearchParams()
            let input = new FormData(evt.target);

            params.set('barge', input.get('barge'))

            fetch(`api/rusal/excel/download-by-barge?${params}`)
            .then(
                (response) => response.blob()
            ).then((blob) => {
                const url = window.URL.createObjectURL(new Blob([blob]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', `rusal-barge-${input.get('barge')}.xlsx`)

                document.body.appendChild(link);

                link.click();

                link.parentNode.removeChild(link);
            }).catch(
                (error) => {
                    alert(error)
                }
            )
            evt.target.reset();
            return false;
        }

        return (
            <form onSubmit={ handleSubmit }>
                <input id='barge' name='barge' type='text' placeholder='Enter Barge Identifier: '/>
                &nbsp;&nbsp;&nbsp;
                <button id='download-barge' type='submit'>Download By Barge</button> 
            </form>
        )
    }

    return (
        <div id='downloads'>
            <h3>Download Links</h3>
            <DownloadDatabase />
            &nbsp;&nbsp;&nbsp;
            <DownloadByOrderAndLoad />
            &nbsp;&nbsp;&nbsp;
            <DownloadByBarge />
        </div>
    );
}