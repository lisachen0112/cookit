import React from 'react'

const Hero = ({ 
  title = 'Cooking made easy', 
  subtitle = 'Recreate delicious recipes or share your own'
}) => {
  return (
    <section className="bg-white py-20 text-medium-custom border-b border-gray-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col items-center">
        <div className="text-center">
          <h1 className="text-4xl font-extrabold sm:text-5xl md:text-6xl">{title}</h1>
          <p className="my-4 text-xl">{subtitle}</p>
        </div>
      </div>
    </section>
  )
}

export default Hero