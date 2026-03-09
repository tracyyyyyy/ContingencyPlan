package com.example.contingencyplan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onScenarioSelected: (Scenario) -> Unit,
    onCueCardSelected: (String) -> Unit
){

    val scenarios = listOf(
        Scenario("A1 Station on Fire (TH)", "a1_station_on_fire.json"),
        Scenario("A2 Station on Fire (NTH)", "a2_station_on_fire.json"),
        Scenario("A3 Train on Fire at Platform", "a3_train_on_fire_platform.json"),
        Scenario("A4 Train on Fire Between Stations", "a4_train_on_fire_between.json"),
        Scenario("A5 CBRN", "a5_cbrn.json"),
        Scenario("A6 Bomb Threat", "a6_bomb_threat.json"),
        Scenario("A7 SCR Evacuation", "a7_scr_evacuation.json"),

        Scenario("B1 Train Service Suspension", "b1_train_service_suspension.json"),
        Scenario("B2 Train-to-Track Derailment", "b2_train_derailment.json"),
        Scenario("B3 Total Power Supply Failure", "b3_total_power_failure.json"),
        Scenario("B4 Partial Power Supply Failure", "b4_partial_power_failure.json"),

        Scenario("C1 Person Under Train", "c1_person_under_train.json"),
        Scenario("C2 MTR Shuttle Bus Operation", "c2_shuttle_bus.json"),
        Scenario("C3 OCC Evacuation", "c3_occ_evacuation.json"),
        Scenario("C4 Point Failure", "c4_point_failure.json"),
        Scenario("C5 Bi-directional Operation", "c5_bi_directional.json"),

        Scenario("D1 Integrated Crowd Management Plan A", "d1_crowd_plan_a.json"),
        Scenario("D2 Integrated Crowd Management Plan B", "d2_crowd_plan_b.json"),
        Scenario("D3 Station Crowd Control", "d3_station_crowd_control.json"),
        Scenario("D4 Incident Outside Station", "d4_incident_outside.json"),
        Scenario("D5 Flooding", "d5_flooding.json"),
        Scenario("D5A Disastrous Flooding", "d5a_disastrous_flooding.json"),
        Scenario("D6 Tropical Cyclone Signal No. 8", "d6_typhoon.json"),
        Scenario("D7 Protest", "d7_protest.json"),
        Scenario("D8 Handling of POE / Riot", "d8_poe_riot.json"),
        Scenario("D9 Station Security Management Plan", "d9_security.json"),
        Scenario("D10 Multiple Entrance Close", "d10_entrance_close.json")
    )

    val cueCards = listOf(
        CueCard("Sick Person"),
        CueCard("Police"),
        CueCard("FSD"),
        CueCard("LP on Track"),
        CueCard("Missing Person")
    )

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = "Please select",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {

            // Cue Card section
            item {

                ExpandableCueCardSection(
                    title = "Cue Cards",
                    cueCards = cueCards,
                    onCueCardSelected = onCueCardSelected
                )
            }

            // Contingency Plans title
            item {

                Text(
                    text = "Contingency Plans",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            item {

                ExpandableScenarioSection(
                    title = "A Emergency evacuation",
                    scenarios = scenarios.filter { it.title.startsWith("A") },
                    onScenarioSelected = onScenarioSelected
                )
            }

            item {

                ExpandableScenarioSection(
                    title = "B Non-emergency evacuation",
                    scenarios = scenarios.filter { it.title.startsWith("B") },
                    onScenarioSelected = onScenarioSelected
                )
            }

            item {

                ExpandableScenarioSection(
                    title = "C Equipment failure & incident",
                    scenarios = scenarios.filter { it.title.startsWith("C") },
                    onScenarioSelected = onScenarioSelected
                )
            }

            item {

                ExpandableScenarioSection(
                    title = "D Crowd management",
                    scenarios = scenarios.filter { it.title.startsWith("D") },
                    onScenarioSelected = onScenarioSelected
                )
            }
        }
    }
}

@Composable
fun ExpandableScenarioSection(
    title: String,
    scenarios: List<Scenario>,
    onScenarioSelected: (Scenario) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(12.dp)
        )

        if (expanded) {

            scenarios.forEach { scenario ->

                Card(
                    onClick = { onScenarioSelected(scenario) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {

                    Text(
                        text = scenario.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableCueCardSection(
    title: String,
    cueCards: List<CueCard>,
    onCueCardSelected: (String) -> Unit
){

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(12.dp)
        )

        if (expanded) {

            cueCards.forEach { card ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    onClick = { onCueCardSelected(card.title) }
                ){

                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}