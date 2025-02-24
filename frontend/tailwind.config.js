/** @type {import('tailwindcss').Config} */
export default {
    content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
    theme: {
      extend: {
        colors: {
          'light-custom': '#FEFAF6',
          'medium-custom': '#102C57',
          'text-custom': '#484848',
          'text-dark-custom': '#202020',
        },
        fontFamily: {
          sans: ['Montserrat', 'sans-serif'],
          title: ["Oswald", "sans-serif"],
        },
        gridTemplateColumns: {
          '70/30': '70% 28%',
        },
      },
    },
    plugins: [],
  };