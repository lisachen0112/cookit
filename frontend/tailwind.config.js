/** @type {import('tailwindcss').Config} */
export default {
    content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
    theme: {
      extend: {
        colors: {
          'dark-custom': '#231942',
          'light-custom': '#9f86c0',
          'medium-custom': '#5e548e',
          'white-custom': '#FDF0D5',
          'text-custom': '#484848',
          'text-dark-custom': '#202020',
        },
        fontFamily: {
          sans: ['Roboto', 'sans-serif'],
        },
        gridTemplateColumns: {
          '70/30': '70% 28%',
        },
      },
    },
    plugins: [],
  };