import React from 'react'
import ClipLoader from 'react-spinners/ClipLoader';

const Spinner = ({ loading }) => {
  return (
    <div className="flex justify-center items-center mt-60">
        <ClipLoader 
        color='#102C57'
        loading={loading} 
        size={150}
        />
    </div>
  )
}

export default Spinner