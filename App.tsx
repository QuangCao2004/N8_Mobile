/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useEffect, useState } from 'react';
import { View, Text, ActivityIndicator, StyleSheet } from 'react-native';

export default function App() {
  const [weather, setWeather] = useState(null);
  const [loading, setLoading] = useState(true);

  // Replace with your city coordinates
  const latitude = 10.82;
  const longitude = 106.62;

  useEffect(() => {
    const fetchWeather = async () => {
      try {
        const response = await fetch(
          `https://api.open-meteo.com/v1/forecast?latitude=${latitude}&longitude=${longitude}&current_weather=true`
        );
        const data = await response.json();
        setWeather(data.current_weather);
      } catch (error) {
        console.error('Error fetching weather:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchWeather();
  }, []);

  if (loading) return <ActivityIndicator style={styles.loader} size="large" />;

  return (
    <View style={styles.container}>
      {weather ? (
        <>
          <Text style={styles.text}>🌡 Temperature: {weather.temperature}°C</Text>
          <Text style={styles.text}>💨 Wind: {weather.windspeed} km/h</Text>
          <Text style={styles.text}>🌥 Weather Code: {weather.weathercode}</Text>
        </>
      ) : (
        <Text style={styles.text}>No weather data available</Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  text: { fontSize: 18, marginBottom: 10 },
  loader: { flex: 1, justifyContent: 'center' },
});
