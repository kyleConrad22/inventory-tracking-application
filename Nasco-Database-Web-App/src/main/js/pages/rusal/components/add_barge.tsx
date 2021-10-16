import React from 'react'

export default function AddBarge() {

    function handleSubmit(evt) {
        
        evt.preventDefault()

        fetch('/api/rusal/update/barge', {
            method: 'POST',
            body: new FormData(evt.target)
        }).then(
            (response) => {
                if (response.ok) {
                    alert('Successfully added barge identifier to BL')
                } else {
                    alert('Failed to add barge identifier to BL')
                }
            }
        ).catch(
            (error) => {
                alert(error)
            }
        )
        evt.target.reset();
        return false;
    }

    return (
        <form onSubmit={handleSubmit}>
            <input id='bl' name='bl' type='text' placeholder='Enter BL Number:' />
            &nbsp;&nbsp;&nbsp;
            <input id='barge' name='barge' type='text' placeholder='Enter Barge Identifier:' />
            &nbsp;&nbsp;&nbsp;
            <button type='submit'>Add Barge Identifer to BL</button>
        </form>
    )
}