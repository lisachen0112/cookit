/** @type {import('tailwindcss').Config} */
export default {
    content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
    theme: {
      extend: {
        colors: {
          'dark-custom': '#231942',
          'light-custom': '#FEFAF6',
          'medium-custom': '#102C57',
          'white-custom': '#FDF0D5',
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