import React, {useRef} from "react";

import ToBeImplemented from "../../util/to_be_implemented";

export default function ExcelSpreadSheetFormatting() {
    
    function FormatAlgomaReport() {

        const inputFile = useRef(null);


        function handleClick() {
            inputFile.current.click()
        }

        return (
            <div>
                <input id='file' type='file' ref={ inputFile } style={ { display: 'none' } }/>
                <button onClick={ handleClick }>Format Algoma Report</button>
            </div>
        );
    }

    function FormatSsabReport() {

        const inputFile = useRef(null);

        <input id='file' type='file' accept='.xlsx' ref={ inputFile } style={ { display: 'none' } }/>

        function handleClick() {
            inputFile.current.click()
        }

        return (
            <div>
                <input id='file' type='file' accept='.xlsx' ref={ inputFile } style={ { display: 'none' } }/>
                <button onClick={ handleClick }>Format SSAB Report</button>
            </div>
        );
    }
    
    return (
        <div>
            <h3>Excel SpreadSheet Formatting</h3>
            <FormatAlgomaReport />
            &nbsp;&nbsp;&nbsp;
            <FormatSsabReport />
        </div>
    );
}