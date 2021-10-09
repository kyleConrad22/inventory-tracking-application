import React, {useRef} from "react";

export default function ExcelSpreadSheetFormatting() {
    
    function FormatAlgomaReport() {

        const inputFile = useRef();

        function handleChange(evt) {
            const files = evt.target.files
            const formData = new FormData()
            for (let i = 0; i < files.length; i++) {
                formData.append('file', files[i])
            }
        
            fetch('/api/excel/algoma', {
                method: 'POST',
                body: formData
            }).then(
                (response) => response.blob()
            ).then(
                blob => {
                    const url = window.URL.createObjectURL(new Blob([blob]))
                    const link = document.createElement('a')

                    link.href = url;
                    link.setAttribute('download', 'algoma-report.xlsx');

                    document.body.appendChild(link);

                    link.click();

                    link.parentNode.removeChild(link);
                }
            ).catch(
                (error) => {
                    alert(error)
                }
            )

            return false;
        }

        function handleClick() {
            inputFile.current.click()
        }

        return (
            <div>
                <input id='algomaFile' type='file' multiple ref={ inputFile } style={ { display: 'none' } } onChange={ handleChange } />
                <button onClick={ handleClick }>Format Algoma Report</button>
            </div>
        );
    }

    function FormatSsabReport() {

        const inputFile = useRef();

        function handleChange(evt) {
            const files = evt.target.files
            const formData = new FormData()
            formData.append('file', files[0])

            fetch('/api/excel/ssab', {
                method: 'POST',
                body: formData
            }).then(
                (response) => response.blob()
            ).then(
                blob => {
                    const url = window.URL.createObjectURL(new Blob([blob]));
                    const link = document.createElement('a');

                    link.href = url;
                    link.setAttribute('download', 'ssab-report.xlsx');

                    document.body.appendChild(link);

                    link.click();

                    link.parentNode.removeChild(link);

                }
            ).catch(
                (error) => {
                    alert(error)
                }
            )
            return false;
        }

        function handleClick() {
            inputFile.current.click()
        }

        return (
            <div>
                <input id='ssabFile' type='file' accept='.xlsx' ref={ inputFile } style={ { display: 'none' } } onChange={ handleChange } />
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