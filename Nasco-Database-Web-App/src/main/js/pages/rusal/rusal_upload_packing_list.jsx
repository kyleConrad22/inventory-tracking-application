import React from "react";
import ToBeImplemented from "../../core/to_be_implemented";

export default function RusalUploadPackingList() {

    function UploadButton() {
        
        function handleClick() {
            console.log("Button Clicked!")
        }

        return (
            <form>
                <input id='vessel' name='vessel' type='text' placeholder='Enter Vessel Name:' />
                &nbsp;&nbsp;&nbsp;
                <button id="upload-packing-list" type="button" onClick={ () => { handleClick() } }>
                    Upload Packing List
                </button>
            </form>
        );
    }

    return (
        <div>
            <h3>Upload Packing List</h3>
            <ToBeImplemented />
            <UploadButton />
        </div>
    );
}