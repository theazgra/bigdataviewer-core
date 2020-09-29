# BigDataViewer N5 format

The BigDataViewer N5 back-end (`format="bdv.n5"`) requires a N5 dataset with a specific group hierarchy and attributes.

Multi-channel (-angle, -tile, -illumination) time-series as
multi-resolution 3D stacks are organized in the following N5 hierarchy:

```
\
├── setup0
│   ├── attributes.json {"downsamplingFactors":[[1,1,1],[2,2,2]],"dataType":"uint8"}
│   ├── timepoint0
│   │   ├── s0
│   │   │   ├── attributes.json {"dataType":"uint8","compression":{"type":"bzip2","blockSize":9},"blockSize":[16,16,16],"dimensions":[400,400,25]}
│   │   │   ┊
│   │   │
│   │   ├── s1
│   │   ┊
│   │
│   ├── timepoint1
│   ┊
│
├── setup1
┊
```

Each 3D stack is stored as a dataset with path formatted as `/setup%d/timepoint%d/s%d`.
Here, `setup` number is flattened integer ID of channel, angle, tile, illumination, etc.,
`timepoint` is the integer ID of the time point, and `s` is the scale level of the multi-resolution pyramid.

## setup attributes
Each `setup` group has attributes
```
"downsamplingFactors" : [[1,1,1], [2,2,1], ...]
"dataType":"uint8"
```
that specify downsampling scheme and datatype, which are the same for all timepoints.
`downsamplingFactors` specifies power-of-two downscaling factors for each scale level (with respect to full resolution `s0`, which always has `[1,1,1]`).
`dataType` is one of {uint8, uint16, uint32, uint64, int8, int16, int32, int64, float32, float64}.

> TODO: Additional metadata (for example identifying `setup3` as channel 3, tile 124, etc.) could be replicated from the XML.
The idea would be that parts of a dataset can be used independent of BDV, without the XML.
We should agree on standard attributes for this.

## timepoint attributes
`timepoint` has no mandatory attributes.

For compatibility with Paintera, when exporting to N5 we put the `"multiScale" : true`, and `"resolution" : [x,y,z]` attributes.

## scale level attributes
`s0`, `s1`, etc. have the mandatory N5 dataset attributes.

For compatibility with Paintera, when exporting to N5 we put the `"downsamplingFactors": [x,y,z]` attribute.

> TODO: Additional metadata (for example scaled resolution and affine transform) could be replicated from the XML.
The idea would be that an individual stack can be used independent of BDV, without the XML.
We should agree on standard attributes for this.
