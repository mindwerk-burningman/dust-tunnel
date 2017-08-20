# DustTunnelTodo #

<!-- * processing -->
<!-- * processing in DustTunnel -->
<!-- * osc listening -->
<!-- * osc to midi -->
<!-- * midi notes to logic -->
<!-- * osc to midi cc -->
<!-- * midi cc to logic -->
<!-- * assign cc messages to controls/automation -->
<!-- * headset to muze io -->
<!-- * headset to DustTunnel -->
* headset to DustTunnel to make a sound
* configure muze io
* record and playback - http://developer.choosemuse.com/research-tools/museplayer
* start programs from sh script

## Music ##

* multiple tracks/instruments with controls
* random midi notes separate tracks
* create pad sounds / swells
* create chords
* create melodies
* all sounds have long envelopes
* automation tracks?

## DustTunnel ##

* `NoteFactory`
* `ControlChangeFactory`
* smoothing algo inbetween CC messages
* CC messages to assigned controls in logic
  - tempo
  - envelopes
  - modulation
* start/stop/control automation tracks
* random note selection from arrays of dorian scales
* random duration/velocity
* create chord arrays
* create scale arrays
* filter headset values

## Commands ##

`muse-io --device Muse-98A9`

sending from device to 
`muse-io --device Muse-98A9 --osc osc.udp://localhost:9000`

reading file over TCP
`muse-player -f data_recording.muse -s osc.tcp://127.0.0.1:9000`