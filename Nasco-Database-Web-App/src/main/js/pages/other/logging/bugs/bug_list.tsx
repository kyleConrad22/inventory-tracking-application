import React from "react"
import BugListItem from "./bug_list_item"

export default function BugList(props) {
    if (!props.bugListItems) {
        return (
            <div>
                <h3>No Bugs found...</h3>
            </div>
        )
    }
}