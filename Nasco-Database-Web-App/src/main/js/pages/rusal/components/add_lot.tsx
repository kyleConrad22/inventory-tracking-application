import React from 'react'

export default function AddLot() {

    function handleSubmit(evt) {
        evt.preventDefault()

        fetch('/api/rusal/update/lot/site', {
            method: 'POST',
            body: new FormData(evt.target)
        }).then(
            (response) => {
                if (response.ok) {
                    alert('Successfully added lot to heats')
                } else {
                    alert('Failed to add lot to heats')
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
            <input id='lot' name='lot' type='text' placeholder='Enter Lot:'/>
            &nbsp;&nbsp;&nbsp;
            <input id='bl' name='bl' type='text' placeholder='Enter BL Number:'/>
            &nbsp;&nbsp;&nbsp;
            <input id='heat' name='heat' type='text'placeholder='Enter Heat Number:'/>
            &nbsp;&nbsp;&nbsp;
            <button type='submit'>Add Lot</button>
        </form>
    )
}