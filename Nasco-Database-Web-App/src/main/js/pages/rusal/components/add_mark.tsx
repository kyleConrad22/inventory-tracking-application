import React from 'react';

export default function AddMark() {

    function handleSubmit(evt) {
        evt.preventDefault();

        fetch('/api/rusal/update/mark', {
            method: 'POST',
            body: new FormData(evt.target)
        }).then(
            (response) => {
                if (response.ok) {
                    alert('Successfully added mark to BL!')
                } else {
                    alert('Failed to add mark to BL')
                }
            }
        ).catch (
            (error) => {
                alert(error)
            }
        )
        evt.target.reset()
        return false;
    }

    return(
        <form onSubmit={ handleSubmit }>
            <input id='bl' name='bl' type='text' placeholder='Enter BL Number:' />
            &nbsp;&nbsp;&nbsp;
            <input id='mark' name='mark' type='text' placeholder='Enter Mark:' />
            &nbsp;&nbsp;&nbsp;
            <button id='add-mark' type='submit'>Add Mark to BL</button>
        </form>
    )
}