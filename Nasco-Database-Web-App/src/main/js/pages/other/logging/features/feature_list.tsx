import React from "react"
import FeatureListItem from "./feature_list_item"

export default function FeatureList(props  : any) {
    if (!props.featureListItems) {
        return ( 
            <div>
                <h3>No features found...</h3>
            </div>
        )
    } 

    //const featureListItems = props.featureListItems.map(featureListItem =>
    //        <FeatureListItem 
    //    )
}