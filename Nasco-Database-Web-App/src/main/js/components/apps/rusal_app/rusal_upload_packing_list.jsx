import React from "react";
import ToBeImplemented from "../../util/to_be_implemented";

export default function RusalUploadPackingList() {

    function UploadButton() {
        
        function handleClick() {
            console.log("Button Clicked!")
        }

        return (
            <button id="upload-packing-list" type="button" onClick={ () => { handleClick() } }>
                Upload Packing List
            </button>
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